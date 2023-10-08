/*
 * Copyright 2023 Squircle CE contributors.
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
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.editorkit.model.FindResult
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.data.utils.charsetFor
import com.blacksquircle.ui.feature.editor.data.utils.decode
import com.blacksquircle.ui.feature.editor.data.utils.encode
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentParams
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.explorer.domain.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern

class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val filesystemFactory: FilesystemFactory,
    private val cacheFilesystem: Filesystem,
    private val context: Context,
) : DocumentRepository {

    init {
        Directories.migrateFilenames(context)
    }

    override suspend fun loadDocuments(): List<DocumentModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.documentDao().loadAll()
                .map(DocumentConverter::toModel)
        }
    }

    override suspend fun updateDocument(documentModel: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            val documentEntity = DocumentConverter.toEntity(documentModel)
            appDatabase.documentDao().insert(documentEntity)
        }
    }

    override suspend fun deleteDocument(documentModel: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            deleteCacheFiles(documentModel)

            val documentEntity = DocumentConverter.toEntity(documentModel)
            appDatabase.documentDao().delete(documentEntity)
        }
    }

    override suspend fun openFile(fileUri: Uri): DocumentModel {
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
            DocumentConverter.toModel(fileModel)
        }
    }

    override suspend fun loadFile(documentModel: DocumentModel): DocumentContent {
        return withContext(dispatcherProvider.io()) {
            val textCacheFile = cacheFile(documentModel, postfix = "text.txt")
            if (cacheFilesystem.exists(textCacheFile)) {
                DocumentContent(
                    documentModel = documentModel,
                    undoStack = loadUndoStack(documentModel),
                    redoStack = loadRedoStack(documentModel),
                    text = cacheFilesystem.loadFile(textCacheFile, FileParams()),
                )
            } else {
                val filesystem = filesystemFactory.create(documentModel.filesystemUuid)
                val fileModel = DocumentConverter.toModel(documentModel)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening),
                )
                DocumentContent(
                    documentModel = documentModel,
                    undoStack = UndoStack(),
                    redoStack = UndoStack(),
                    text = filesystem.loadFile(fileModel, fileParams),
                ).also { content ->
                    saveFile(content, DocumentParams(local = false, cache = true))
                }
            }
        }
    }

    override suspend fun saveFile(content: DocumentContent, params: DocumentParams) {
        withContext(dispatcherProvider.io()) {
            if (params.local) {
                val filesystem = filesystemFactory.create(content.documentModel.filesystemUuid)
                val fileModel = DocumentConverter.toModel(content.documentModel)
                val fileParams = FileParams(
                    charset = charsetFor(settingsManager.encodingForSaving),
                    linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
                )
                filesystem.saveFile(fileModel, content.text, fileParams)
            }
            if (params.cache) {
                createCacheFiles(content.documentModel)

                val textCacheFile = cacheFile(content.documentModel, postfix = "text.txt")
                val undoCacheFile = cacheFile(content.documentModel, postfix = "undo.txt")
                val redoCacheFile = cacheFile(content.documentModel, postfix = "redo.txt")

                cacheFilesystem.saveFile(textCacheFile, content.text, FileParams())
                cacheFilesystem.saveFile(undoCacheFile, content.undoStack.encode(), FileParams())
                cacheFilesystem.saveFile(redoCacheFile, content.redoStack.encode(), FileParams())
            }
        }
    }

    override suspend fun saveFileAs(documentModel: DocumentModel, fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            val textCacheFile = cacheFile(documentModel, postfix = "text.txt")
            val pureTextUtf8 = cacheFilesystem.loadFile(textCacheFile, FileParams())
            val encoding = charsetFor(settingsManager.encodingForSaving)
            val linebreak = LineBreak.of(settingsManager.lineBreakForSaving)
            context.contentResolver.openOutputStream(fileUri)?.use { output ->
                output.write(linebreak.replace(pureTextUtf8).toByteArray(encoding))
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
            if (cacheFilesystem.exists(undoCacheFile)) {
                return cacheFilesystem.loadFile(undoCacheFile, FileParams()).decode()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun loadRedoStack(document: DocumentModel): UndoStack {
        return try {
            val redoCacheFile = cacheFile(document, postfix = "redo.txt")
            if (cacheFilesystem.exists(redoCacheFile)) {
                return cacheFilesystem.loadFile(redoCacheFile, FileParams()).decode()
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

        if (!cacheFilesystem.exists(textCacheFile)) cacheFilesystem.createFile(textCacheFile)
        if (!cacheFilesystem.exists(undoCacheFile)) cacheFilesystem.createFile(undoCacheFile)
        if (!cacheFilesystem.exists(redoCacheFile)) cacheFilesystem.createFile(redoCacheFile)
    }

    private fun deleteCacheFiles(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = "text.txt")
        val undoCacheFile = cacheFile(document, postfix = "undo.txt")
        val redoCacheFile = cacheFile(document, postfix = "redo.txt")

        if (cacheFilesystem.exists(textCacheFile)) cacheFilesystem.deleteFile(textCacheFile)
        if (cacheFilesystem.exists(undoCacheFile)) cacheFilesystem.deleteFile(undoCacheFile)
        if (cacheFilesystem.exists(redoCacheFile)) cacheFilesystem.deleteFile(redoCacheFile)
    }

    private fun cacheFile(document: DocumentModel, postfix: String): FileModel {
        val defaultLocation = cacheFilesystem.defaultLocation()
        return FileModel(
            fileUri = defaultLocation.fileUri + "/" + "${document.uuid}-$postfix",
            filesystemUuid = defaultLocation.filesystemUuid,
        )
    }
}