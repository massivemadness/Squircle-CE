/*
 * Copyright 2022 Squircle CE contributors.
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

import com.blacksquircle.ui.core.data.factory.FilesystemFactory
import com.blacksquircle.ui.core.data.factory.LanguageFactory
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.editorkit.model.UndoStack
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.data.utils.charsetFor
import com.blacksquircle.ui.feature.editor.data.utils.decodeStack
import com.blacksquircle.ui.feature.editor.data.utils.encodeStack
import com.blacksquircle.ui.feature.editor.domain.model.DocumentContent
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.model.DocumentParams
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import kotlinx.coroutines.withContext

class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val filesystemFactory: FilesystemFactory,
    private val cacheFilesystem: Filesystem,
) : DocumentRepository {

    override suspend fun fetchDocuments(): List<DocumentModel> {
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

    override suspend fun loadFile(documentModel: DocumentModel): DocumentContent {
        return withContext(dispatcherProvider.io()) {
            val cacheFile = cacheFile(documentModel, postfix = "text")
            if (cacheFilesystem.exists(cacheFile)) {
                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageFactory.create(documentModel.name),
                    undoStack = loadUndoStack(documentModel),
                    redoStack = loadRedoStack(documentModel),
                    text = cacheFilesystem.loadFile(cacheFile, FileParams())
                )
            } else {
                updateDocument(documentModel)

                val filesystem = filesystemFactory.create(documentModel.filesystemUuid)
                val fileModel = DocumentConverter.toModel(documentModel)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening)
                )

                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageFactory.create(documentModel.name),
                    undoStack = UndoStack(),
                    redoStack = UndoStack(),
                    text = filesystem.loadFile(fileModel, fileParams)
                )
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
                    linebreak = LineBreak.find(settingsManager.lineBreakForSaving)
                )
                filesystem.saveFile(fileModel, content.text, fileParams)
            }
            if (params.cache) {
                createCacheFiles(content.documentModel)

                val textCacheFile = cacheFile(content.documentModel, postfix = "text")
                cacheFilesystem.saveFile(textCacheFile, content.text, FileParams())

                val undoCacheFile = cacheFile(content.documentModel, postfix = "undo")
                val undoStackText = content.undoStack.encodeStack()
                cacheFilesystem.saveFile(undoCacheFile, undoStackText, FileParams())

                val redoCacheFile = cacheFile(content.documentModel, postfix = "redo")
                val redoStackText = content.redoStack.encodeStack()
                cacheFilesystem.saveFile(redoCacheFile, redoStackText, FileParams())

                updateDocument(content.documentModel)
            }
        }
    }

    private suspend fun loadUndoStack(documentModel: DocumentModel): UndoStack {
        return try {
            val undoCacheFile = cacheFile(documentModel, postfix = "undo")
            if (cacheFilesystem.exists(undoCacheFile)) {
                return cacheFilesystem.loadFile(undoCacheFile, FileParams())
                    .decodeStack()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private suspend fun loadRedoStack(documentModel: DocumentModel): UndoStack {
        return try {
            val redoCacheFile = cacheFile(documentModel, postfix = "redo")
            if (cacheFilesystem.exists(redoCacheFile)) {
                return cacheFilesystem.loadFile(redoCacheFile, FileParams())
                    .decodeStack()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private suspend fun createCacheFiles(documentModel: DocumentModel) {
        val textCacheFile = cacheFile(documentModel, postfix = "text")
        val undoCacheFile = cacheFile(documentModel, postfix = "undo")
        val redoCacheFile = cacheFile(documentModel, postfix = "redo")

        if (!cacheFilesystem.exists(textCacheFile)) { cacheFilesystem.createFile(textCacheFile) }
        if (!cacheFilesystem.exists(undoCacheFile)) { cacheFilesystem.createFile(undoCacheFile) }
        if (!cacheFilesystem.exists(redoCacheFile)) { cacheFilesystem.createFile(redoCacheFile) }
    }

    private suspend fun deleteCacheFiles(documentModel: DocumentModel) {
        val textCacheFile = cacheFile(documentModel, postfix = "text")
        val undoCacheFile = cacheFile(documentModel, postfix = "undo")
        val redoCacheFile = cacheFile(documentModel, postfix = "redo")

        if (cacheFilesystem.exists(textCacheFile)) { cacheFilesystem.deleteFile(textCacheFile) }
        if (cacheFilesystem.exists(undoCacheFile)) { cacheFilesystem.deleteFile(undoCacheFile) }
        if (cacheFilesystem.exists(redoCacheFile)) { cacheFilesystem.deleteFile(redoCacheFile) }
    }

    private suspend fun cacheFile(documentModel: DocumentModel, postfix: String): FileModel {
        val defaultLocation = cacheFilesystem.defaultLocation()
        return FileModel(
            fileUri = defaultLocation.fileUri + "/" + "${documentModel.uuid}-$postfix.cache",
            filesystemUuid = defaultLocation.filesystemUuid
        )
    }
}