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
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.domain.repository.documents.DocumentRepository
import com.brackeys.ui.editorkit.model.TextChange
import com.brackeys.ui.editorkit.utils.UndoStack
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileParams
import com.brackeys.ui.filesystem.local.converter.FileConverter
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.IOException

class CacheRepository(
    private val cacheDirectory: File,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val file = cache("${documentModel.uuid}.cache")
        val fileModel = FileConverter.toModel(file)

        return filesystem.loadFile(fileModel, FileParams())
            .map { text ->
                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageDelegate.provideLanguage(documentModel.name),
                    undoStack = loadUndoStack(documentModel),
                    redoStack = loadRedoStack(documentModel),
                    text = text
                )
            }
    }

    override fun saveFile(documentContent: DocumentContent): Completable {
        val documentModel = documentContent.documentModel
        val undoStack = documentContent.undoStack
        val redoStack = documentContent.redoStack
        val text = documentContent.text

        val file = cache("${documentModel.uuid}.cache")
        val fileModel = FileConverter.toModel(file)
        val documentEntity = DocumentConverter.toEntity(documentModel)

        return Completable.concatArray(
                filesystem.saveFile(fileModel, text, FileParams()),
                saveUndoStack(documentModel, undoStack),
                saveRedoStack(documentModel, redoStack)
            )
            .doOnComplete { appDatabase.documentDao().update(documentEntity) }
    }

    fun deleteCache(documentModel: DocumentModel): Completable {
        return try {
            val documentEntity = DocumentConverter.toEntity(documentModel)

            val textCacheFile = cache("${documentModel.uuid}.cache")
            val undoCacheFile = cache("${documentModel.uuid}-undo.cache")
            val redoCacheFile = cache("${documentModel.uuid}-redo.cache")

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
        return cache("${documentModel.uuid}.cache").exists()
    }

    private fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val undoFile = cache("${documentModel.uuid}-undo.cache")
            undoFile.writeText(encodeStack(undoStack))

            Completable.complete()
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    private fun saveRedoStack(documentModel: DocumentModel, redoStack: UndoStack): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val redoFile = cache("${documentModel.uuid}-redo.cache")
            redoFile.writeText(encodeStack(redoStack))

            Completable.complete()
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    private fun loadUndoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreUndoStack(documentModel.uuid)
        } catch (e: NumberFormatException) {
            UndoStack()
        }
    }

    private fun loadRedoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreRedoStack(documentModel.uuid)
        } catch (e: NumberFormatException) {
            UndoStack()
        }
    }

    private fun restoreUndoStack(uuid: String): UndoStack {
        val file = cache("$uuid-undo.cache")
        if (file.exists()) {
            return decodeStack(file.readText())
        }
        return UndoStack()
    }

    private fun restoreRedoStack(uuid: String): UndoStack {
        val file = cache("$uuid-redo.cache")
        if (file.exists()) {
            return decodeStack(file.readText())
        }
        return UndoStack()
    }

    private fun encodeStack(stack: UndoStack): String {
        val builder = StringBuilder()
        val delimiter = "\u0005"
        for (i in stack.size - 1 downTo 0) {
            val textChange = stack[i]
            builder.append(textChange.oldText)
            builder.append(delimiter)
            builder.append(textChange.newText)
            builder.append(delimiter)
            builder.append(textChange.start)
            builder.append(delimiter)
        }
        if (builder.isNotEmpty()) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }

    private fun decodeStack(raw: String): UndoStack {
        val result = UndoStack()
        if (raw.isNotEmpty()) {
            val items = raw.split("\u0005").toTypedArray()
            if (items[items.size - 1].endsWith("\n")) {
                val item = items[items.size - 1]
                items[items.size - 1] = item.substring(0, item.length - 1)
            }
            for (i in items.size - 3 downTo 0 step 3) {
                val change = TextChange(
                    newText = items[i + 1],
                    oldText = items[i],
                    start = items[i + 2].toInt()
                )
                result.push(change)
            }
        }
        return result
    }

    private fun createCacheFilesIfNecessary(documentModel: DocumentModel) {
        val textCacheFile = cache("${documentModel.uuid}.cache")
        val undoCacheFile = cache("${documentModel.uuid}-undo.cache")
        val redoCacheFile = cache("${documentModel.uuid}-redo.cache")

        if (!textCacheFile.exists()) { textCacheFile.createNewFile() } // Create text cache
        if (!undoCacheFile.exists()) { undoCacheFile.createNewFile() } // Create undo-stack cache
        if (!redoCacheFile.exists()) { redoCacheFile.createNewFile() } // Create redo-stack cache
    }

    private fun cache(fileName: String): File {
        return File(cacheDirectory, fileName)
    }
}