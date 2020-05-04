/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.repository

import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.feature.language.LanguageProvider
import com.lightteam.modpeide.data.feature.undoredo.UndoStackImpl
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.feature.undoredo.UndoStack
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.domain.editor.DocumentContent
import com.lightteam.modpeide.domain.editor.DocumentModel
import com.lightteam.modpeide.domain.editor.TextChange
import com.lightteam.modpeide.domain.repository.DocumentRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException

class CacheRepository(
    private val cacheDirectory: File,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val cacheModel = documentModel.copy(name = "${documentModel.uuid}.cache")
        val fileModel = DocumentConverter.toModel(cacheModel)
        return filesystem.loadFile(fileModel)
            .map { text ->
                val language = LanguageProvider.provide(documentModel.name)
                val undoStack = loadUndoStack(documentModel)
                val redoStack = loadRedoStack(documentModel)

                return@map DocumentContent(
                    documentModel,
                    language,
                    undoStack,
                    redoStack,
                    text
                )
            }
    }

    override fun saveFile(documentModel: DocumentModel, text: String): Completable {
        val cacheModel = documentModel.copy(name = "${documentModel.uuid}.cache")
        val fileModel = DocumentConverter.toModel(cacheModel)
        return filesystem.saveFile(fileModel, text)
            .doOnComplete {
                appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) // Save to Database
            }
    }

    fun isCached(documentModel: DocumentModel): Boolean {
        return cache("${documentModel.uuid}.cache").exists()
    }

    fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val undoCache = encodeUndoStack(undoStack)
            val undoFile = cache("${documentModel.uuid}-undo.cache")
            val undoWriter = undoFile.outputStream().bufferedWriter()
            undoWriter.write(undoCache)
            undoWriter.close()

            Completable.complete()
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    fun saveRedoStack(documentModel: DocumentModel, redoStack: UndoStack): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val redoCache = encodeUndoStack(redoStack)
            val redoFile = cache("${documentModel.uuid}-redo.cache")
            val redoWriter = redoFile.outputStream().bufferedWriter()
            redoWriter.write(redoCache)
            redoWriter.close()

            Completable.complete()
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    fun deleteCache(documentModel: DocumentModel): Completable {
        return try {
            val textCacheFile = cache("${documentModel.uuid}.cache")
            val undoCacheFile = cache("${documentModel.uuid}-undo.cache")
            val redoCacheFile = cache("${documentModel.uuid}-redo.cache")

            if (textCacheFile.exists()) { textCacheFile.delete() } // Delete text cache
            if (undoCacheFile.exists()) { undoCacheFile.delete() } // Delete undo-stack cache
            if (redoCacheFile.exists()) { redoCacheFile.delete() } // Delete redo-stack cache

            Completable
                .fromAction {
                    appDatabase.documentDao().delete(DocumentConverter.toEntity(documentModel)) // Delete from Database
                }
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    fun deleteAllCaches() {
        cacheDirectory.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

    private fun loadUndoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreUndoStack(documentModel.uuid)
        } catch (e: NumberFormatException) {
            UndoStackImpl()
        }
    }

    private fun loadRedoStack(documentModel: DocumentModel): UndoStack {
        return try {
            restoreRedoStack(documentModel.uuid)
        } catch (e: NumberFormatException) {
            UndoStackImpl()
        }
    }

    private fun restoreUndoStack(uuid: String): UndoStack {
        val file = cache("$uuid-undo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStackImpl()
    }

    private fun restoreRedoStack(uuid: String): UndoStack {
        val file = cache("$uuid-redo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStackImpl()
    }

    private fun readUndoStackCache(file: File): UndoStack {
        val text = file.inputStream().bufferedReader().use(BufferedReader::readText)
        return decodeUndoStack(text)
    }

    private fun encodeUndoStack(stack: UndoStack): String {
        val builder = StringBuilder()
        val delimiter = "\u0005"
        for (i in stack.count() - 1 downTo 0) {
            val textChange = stack.getItemAt(i)
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

    private fun decodeUndoStack(raw: String?): UndoStack {
        val result = UndoStackImpl()
        if (!(raw == null || raw.isEmpty())) {
            val items = raw.split("\u0005").toTypedArray()
            if (items[items.size - 1].endsWith("\n")) {
                val item = items[items.size - 1]
                items[items.size - 1] = item.substring(0, item.length - 1)
            }
            var i = items.size - 3
            while (i >= 0) {
                val change = TextChange(
                    items[i + 1],
                    items[i],
                    Integer.parseInt(items[i + 2])
                )
                result.push(change)
                i -= 3
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