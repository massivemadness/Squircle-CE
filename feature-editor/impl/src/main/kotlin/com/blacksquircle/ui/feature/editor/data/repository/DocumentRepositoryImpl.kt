/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.editor.data.repository

import android.content.Context
import android.net.Uri
import com.blacksquircle.ui.core.extensions.extractFilePath
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.editorkit.model.FindResult
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.data.utils.charsetFor
import com.blacksquircle.ui.feature.editor.data.utils.decode
import com.blacksquircle.ui.feature.editor.data.utils.encode
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentParams
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern

internal class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val documentDao: DocumentDao,
    private val filesystemFactory: FilesystemFactory,
    private val cacheDirectory: File,
    private val context: Context,
) : DocumentRepository {

    override suspend fun loadDocuments(): List<DocumentModel> {
        return withContext(dispatcherProvider.io()) {
            documentDao.loadAll().map(DocumentMapper::toModel)
        }
    }

    override suspend fun loadDocument(document: DocumentModel): DocumentContent {
        return withContext(dispatcherProvider.io()) {
            val documentEntity = DocumentMapper.toEntity(document)
            documentDao.insert(documentEntity)
            settingsManager.selectedUuid = document.uuid

            delay(1500L) // TODO

            val textCacheFile = cacheFile(document, postfix = "text.txt")
            if (textCacheFile.exists()) {
                DocumentContent(
                    undoStack = loadUndoStack(document),
                    redoStack = loadRedoStack(document),
                    text = textCacheFile.readText(),
                )
            } else {
                val filesystem = filesystemFactory.create(document.filesystemUuid)
                val fileModel = DocumentMapper.toModel(document)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening),
                )
                DocumentContent(
                    undoStack = UndoStack(),
                    redoStack = UndoStack(),
                    text = filesystem.loadFile(fileModel, fileParams),
                ).also { content ->
                    saveDocument(document, content, DocumentParams(local = false, cache = true))
                }
            }
        }
    }

    override suspend fun saveDocument(
        document: DocumentModel,
        content: DocumentContent,
        params: DocumentParams
    ) {
        withContext(dispatcherProvider.io()) {
            if (params.local) {
                val filesystem = filesystemFactory.create(document.filesystemUuid)
                val fileModel = DocumentMapper.toModel(document)
                val fileParams = FileParams(
                    charset = charsetFor(settingsManager.encodingForSaving),
                    linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
                )
                filesystem.saveFile(fileModel, content.text, fileParams)
            }
            if (params.cache) {
                createCacheFiles(document)

                val textCacheFile = cacheFile(document, postfix = "text.txt")
                val undoCacheFile = cacheFile(document, postfix = "undo.txt")
                val redoCacheFile = cacheFile(document, postfix = "redo.txt")

                textCacheFile.writeText(content.text)
                undoCacheFile.writeText(content.undoStack.encode())
                redoCacheFile.writeText(content.redoStack.encode())
            }
        }
    }

    override suspend fun reorderDocuments(from: DocumentModel, to: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.reorderDocuments(
                fromUuid = from.uuid,
                toUuid = to.uuid,
                fromIndex = from.position,
                toIndex = to.position,
            )
        }
    }

    override suspend fun closeDocument(document: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.closeDocument(document.uuid, document.position)
            deleteCacheFiles(document)
        }
    }

    override suspend fun closeOtherDocuments(document: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.closeOtherDocuments(document.uuid)
            settingsManager.selectedUuid = document.uuid
            clearAllCaches(document.uuid)
        }
    }

    override suspend fun closeAllDocuments() {
        withContext(dispatcherProvider.io()) {
            documentDao.deleteAll()
            settingsManager.selectedUuid = "null"
            clearAllCaches()
        }
    }

    override suspend fun openExternal(fileUri: Uri): DocumentModel {
        return withContext(dispatcherProvider.io()) {
            Timber.d("Uri received = $fileUri")

            val filePath = context.extractFilePath(fileUri)
            Timber.d("Extracted path = $filePath")

            val isValidFile = try {
                File(filePath).exists()
            } catch (e: Throwable) {
                false
            }
            Timber.d("Is valid file = $isValidFile")

            val fileModel = FileModel("file://$filePath", "local")
            DocumentMapper.toModel(fileModel)
        }
    }

    override suspend fun saveExternal(document: DocumentModel, fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            val textCacheFile = cacheFile(document, postfix = "text.txt")
            val encoding = charsetFor(settingsManager.encodingForSaving)
            val linebreak = LineBreak.of(settingsManager.lineBreakForSaving)
            val byteArray = textCacheFile
                .readText()
                .replace(linebreak.regex, linebreak.replacement)
                .toByteArray(encoding)
            context.contentResolver.openOutputStream(fileUri)?.use { output ->
                output.write(byteArray)
                output.flush()
            }
        }
    }

    override suspend fun find(text: CharSequence, params: FindParams): List<FindResult> {
        return withContext(dispatcherProvider.io()) {
            if (params.query.isEmpty()) {
                return@withContext emptyList()
            }
            val findResults = mutableListOf<FindResult>()
            val pattern = when {
                params.regex && params.matchCase -> Pattern.compile(params.query)
                params.regex && !params.matchCase -> Pattern.compile(
                    params.query, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE,
                )

                params.wordsOnly && params.matchCase -> Pattern.compile("\\s${params.query}\\s")
                params.wordsOnly && !params.matchCase -> Pattern.compile(
                    "\\s" + Pattern.quote(params.query) + "\\s",
                    Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE,
                )

                params.matchCase -> Pattern.compile(Pattern.quote(params.query))
                else -> Pattern.compile(
                    Pattern.quote(params.query),
                    Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE,
                )
            }
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val findResult = FindResult(matcher.start(), matcher.end())
                findResults.add(findResult)
            }
            findResults
        }
    }

    private fun loadUndoStack(document: DocumentModel): UndoStack {
        return try {
            val undoCacheFile = cacheFile(document, postfix = "undo.txt")
            if (undoCacheFile.exists()) {
                return undoCacheFile.readText().decode()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun loadRedoStack(document: DocumentModel): UndoStack {
        return try {
            val redoCacheFile = cacheFile(document, postfix = "redo.txt")
            if (redoCacheFile.exists()) {
                return redoCacheFile.readText().decode()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun createCacheFiles(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = "text.txt")
        val undoCacheFile = cacheFile(document, postfix = "undo.txt")
        val redoCacheFile = cacheFile(document, postfix = "redo.txt")

        if (!textCacheFile.exists()) textCacheFile.createNewFile()
        if (!undoCacheFile.exists()) undoCacheFile.createNewFile()
        if (!redoCacheFile.exists()) redoCacheFile.createNewFile()
    }

    private fun deleteCacheFiles(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = "text.txt")
        val undoCacheFile = cacheFile(document, postfix = "undo.txt")
        val redoCacheFile = cacheFile(document, postfix = "redo.txt")

        if (textCacheFile.exists()) textCacheFile.delete()
        if (undoCacheFile.exists()) undoCacheFile.delete()
        if (redoCacheFile.exists()) redoCacheFile.delete()
    }

    private fun clearAllCaches(filter: String) {
        cacheDirectory.listFiles().orEmpty().forEach { file ->
            if (!file.name.startsWith(filter, ignoreCase = true)) {
                file.deleteRecursively()
            }
        }
    }

    private fun clearAllCaches() {
        cacheDirectory.listFiles().orEmpty().forEach { file ->
            file.deleteRecursively()
        }
    }

    private fun cacheFile(document: DocumentModel, postfix: String): File {
        return File(cacheDirectory, "${document.uuid}-$postfix")
    }
}