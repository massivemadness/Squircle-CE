/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.data.repository.documents

import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.delegate.LanguageDelegate
import com.brackeys.ui.data.utils.decodeStack
import com.brackeys.ui.data.utils.encodeStack
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.domain.repository.documents.DocumentRepository
import com.brackeys.ui.editorkit.utils.UndoStack
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileParams
import com.brackeys.ui.filesystem.local.converter.FileConverter
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

class CacheRepository(
    private val cacheDirectory: File,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val file = File(cacheDirectory, "${documentModel.uuid}.cache")
        val fileModel = FileConverter.toModel(file)

        return Single.create { emitter ->
            val documentContent = DocumentContent(
                documentModel = documentModel,
                language = LanguageDelegate.provideLanguage(documentModel.name),
                undoStack = loadUndoStack(documentModel),
                redoStack = loadRedoStack(documentModel),
                text = runBlocking { filesystem.loadFile(fileModel, FileParams()) }
            )
            emitter.onSuccess(documentContent)
        }
    }

    override fun saveFile(documentContent: DocumentContent): Completable {
        val documentModel = documentContent.documentModel
        val undoStack = documentContent.undoStack
        val redoStack = documentContent.redoStack
        val text = documentContent.text

        val file = File(cacheDirectory, "${documentModel.uuid}.cache")
        val fileModel = FileConverter.toModel(file)
        val documentEntity = DocumentConverter.toEntity(documentModel)

        return Completable.create { emitter ->
            runBlocking { filesystem.saveFile(fileModel, text, FileParams()) }
            saveUndoStack(documentModel, undoStack)
            saveRedoStack(documentModel, redoStack)
            appDatabase.documentDao().update(documentEntity)
            emitter.onComplete()
        }
    }

    fun deleteCache(documentModel: DocumentModel): Completable {
        return try {
            val documentEntity = DocumentConverter.toEntity(documentModel)

            val textCacheFile = File(cacheDirectory, "${documentModel.uuid}.cache")
            val undoCacheFile = File(cacheDirectory, "${documentModel.uuid}-undo.cache")
            val redoCacheFile = File(cacheDirectory, "${documentModel.uuid}-redo.cache")

            Completable.fromAction {
                if (textCacheFile.exists()) { textCacheFile.delete() } // Delete text cache
                if (undoCacheFile.exists()) { undoCacheFile.delete() } // Delete undo-stack cache
                if (redoCacheFile.exists()) { redoCacheFile.delete() } // Delete redo-stack cache

                appDatabase.documentDao().delete(documentEntity)
            }
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    fun isCached(documentModel: DocumentModel): Boolean {
        return File(cacheDirectory, "${documentModel.uuid}.cache").exists()
    }

    private fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack) {
        createCacheFilesIfNecessary(documentModel)

        val undoFile = File(cacheDirectory, "${documentModel.uuid}-undo.cache")
        undoFile.writeText(undoStack.encodeStack())
    }

    private fun saveRedoStack(documentModel: DocumentModel, redoStack: UndoStack) {
        createCacheFilesIfNecessary(documentModel)

        val redoFile = File(cacheDirectory, "${documentModel.uuid}-redo.cache")
        redoFile.writeText(redoStack.encodeStack())
    }

    private fun loadUndoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreUndoStack(documentModel.uuid)
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun loadRedoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreRedoStack(documentModel.uuid)
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private fun restoreUndoStack(uuid: String): UndoStack {
        val file = File(cacheDirectory, "$uuid-undo.cache")
        if (file.exists()) {
            return file.readText().decodeStack()
        }
        return UndoStack()
    }

    private fun restoreRedoStack(uuid: String): UndoStack {
        val file = File(cacheDirectory, "$uuid-redo.cache")
        if (file.exists()) {
            return file.readText().decodeStack()
        }
        return UndoStack()
    }

    private fun createCacheFilesIfNecessary(documentModel: DocumentModel) {
        val textCacheFile = File(cacheDirectory, "${documentModel.uuid}.cache")
        val undoCacheFile = File(cacheDirectory, "${documentModel.uuid}-undo.cache")
        val redoCacheFile = File(cacheDirectory, "${documentModel.uuid}-redo.cache")

        if (!textCacheFile.exists()) { textCacheFile.createNewFile() } // Create text cache
        if (!undoCacheFile.exists()) { undoCacheFile.createNewFile() } // Create undo-stack cache
        if (!redoCacheFile.exists()) { redoCacheFile.createNewFile() } // Create redo-stack cache
    }
}