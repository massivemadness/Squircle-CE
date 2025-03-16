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

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.editorkit.model.FindParams
import com.blacksquircle.ui.editorkit.model.FindResult
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.data.utils.charsetFor
import com.blacksquircle.ui.feature.editor.data.utils.decode
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.saf.SAFFilesystem
import io.github.rosemoe.sora.text.Content
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern

internal class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val documentDao: DocumentDao,
    private val filesystemFactory: FilesystemFactory,
    private val context: Context,
) : DocumentRepository {

    private val cacheDir: File
        get() = Directories.filesDir(context)

    override suspend fun loadDocuments(): List<DocumentModel> {
        return withContext(dispatcherProvider.io()) {
            documentDao.loadAll().map(DocumentMapper::toModel)
        }
    }

    override suspend fun loadDocument(document: DocumentModel): Content {
        return withContext(dispatcherProvider.io()) {
            val documentEntity = DocumentMapper.toEntity(document)
            documentDao.insert(documentEntity)
            settingsManager.selectedUuid = document.uuid

            val textCacheFile = cacheFile(document, postfix = TEXT)
            if (textCacheFile.exists()) {
                val fileContent = textCacheFile.readText()
                Content(fileContent).apply {
                    // TODO undoManager = ...
                    // TODO cursor.setLeft(...)
                    // TODO cursor.setRight(...)
                }
            } else {
                val filesystem = filesystemFactory.create(document.filesystemUuid)
                val fileModel = DocumentMapper.toModel(document)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening),
                )
                val fileContent = filesystem.loadFile(fileModel, fileParams)
                Content(fileContent).also { content ->
                    cacheDocument(document, content)
                }
            }
        }
    }

    override suspend fun saveDocument(document: DocumentModel, content: Content) {
        withContext(dispatcherProvider.io()) {
            val filesystem = filesystemFactory.create(document.filesystemUuid)
            val fileModel = DocumentMapper.toModel(document)
            val fileParams = FileParams(
                charset = charsetFor(settingsManager.encodingForSaving),
                linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
            )
            filesystem.saveFile(fileModel, content.toString(), fileParams).also {
                cacheDocument(document, content)
            }
        }
    }

    override suspend fun cacheDocument(document: DocumentModel, content: Content) {
        withContext(dispatcherProvider.io()) {
            createCacheFiles(document)

            val textCacheFile = cacheFile(document, postfix = TEXT)
            // val undoCacheFile = cacheFile(document, postfix = UNDO)
            // val redoCacheFile = cacheFile(document, postfix = REDO)

            textCacheFile.writeText(content.toString())
            // TODO undoCacheFile.writeText(content.undoStack.encode())
            // TODO redoCacheFile.writeText(content.redoStack.encode())

            // TODO update cursor position in db
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

    override suspend fun openExternal(fileUri: Uri, position: Int): DocumentModel {
        return withContext(dispatcherProvider.io()) {
            val fileModel = when {
                DocumentsContract.isDocumentUri(context, fileUri) -> {
                    try {
                        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(fileUri, flags)
                    } catch (e: SecurityException) {
                        Timber.e(e.message, e)
                    }
                    FileModel(
                        fileUri = fileUri.toString(),
                        filesystemUuid = SAFFilesystem.SAF_UUID,
                    )
                }
                fileUri.scheme == ContentResolver.SCHEME_CONTENT -> {
                    val filePath = context.contentResolver.query(
                        /* uri = */ fileUri,
                        /* projection = */ arrayOf(MediaStore.Files.FileColumns.DATA),
                        /* selection = */ null,
                        /* selectionArgs = */ null,
                        /* sortOrder = */ null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                            if (columnIndex != -1) {
                                return@use cursor.getString(columnIndex)
                            }
                        }
                        return@use null
                    }
                    if (filePath == null) {
                        FileModel(
                            fileUri = fileUri.toString(),
                            filesystemUuid = SAFFilesystem.SAF_UUID,
                        )
                    } else {
                        FileModel(
                            fileUri = LocalFilesystem.LOCAL_SCHEME + filePath,
                            filesystemUuid = LocalFilesystem.LOCAL_UUID,
                        )
                    }
                }
                fileUri.scheme == ContentResolver.SCHEME_FILE -> {
                    FileModel(
                        fileUri = fileUri.toString(),
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    )
                }
                else -> throw IllegalArgumentException("File $fileUri not found")
            }

            DocumentMapper.toModel(fileModel, position = position)
        }
    }

    override suspend fun saveExternal(document: DocumentModel, content: Content, fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            val filesystemUuid = SAFFilesystem.SAF_UUID
            val filesystem = filesystemFactory.create(filesystemUuid)
            val fileModel = DocumentMapper.toModel(document)
            val fileParams = FileParams(
                charset = charsetFor(settingsManager.encodingForSaving),
                linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
            )
            filesystem.saveFile(fileModel, content.toString(), fileParams)
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
            val undoCacheFile = cacheFile(document, postfix = UNDO)
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
            val redoCacheFile = cacheFile(document, postfix = REDO)
            if (redoCacheFile.exists()) {
                return redoCacheFile.readText().decode()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun createCacheFiles(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = TEXT)
        val undoCacheFile = cacheFile(document, postfix = UNDO)
        val redoCacheFile = cacheFile(document, postfix = REDO)

        if (!textCacheFile.exists()) textCacheFile.createNewFile()
        if (!undoCacheFile.exists()) undoCacheFile.createNewFile()
        if (!redoCacheFile.exists()) redoCacheFile.createNewFile()
    }

    private fun deleteCacheFiles(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = TEXT)
        val undoCacheFile = cacheFile(document, postfix = UNDO)
        val redoCacheFile = cacheFile(document, postfix = REDO)

        if (textCacheFile.exists()) textCacheFile.delete()
        if (undoCacheFile.exists()) undoCacheFile.delete()
        if (redoCacheFile.exists()) redoCacheFile.delete()
    }

    private fun clearAllCaches(filter: String) {
        cacheDir.listFiles().orEmpty().forEach { file ->
            if (!file.name.startsWith(filter, ignoreCase = true)) {
                file.deleteRecursively()
            }
        }
    }

    private fun clearAllCaches() {
        cacheDir.listFiles().orEmpty().forEach { file ->
            file.deleteRecursively()
        }
    }

    private fun cacheFile(document: DocumentModel, postfix: String): File {
        return File(cacheDir, "${document.uuid}-$postfix")
    }

    companion object {
        private const val TEXT = "text.txt"
        private const val UNDO = "undo.txt"
        private const val REDO = "redo.txt"
    }
}