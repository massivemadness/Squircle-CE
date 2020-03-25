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

package com.lightteam.modpeide.data.storage.cache

import android.content.Context
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.storage.collection.UndoStack
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.domain.exception.FileNotFoundException
import com.lightteam.modpeide.domain.model.DocumentModel
import io.reactivex.Completable
import io.reactivex.Single
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException

class CacheHandler(
    context: Context,
    private val appDatabase: AppDatabase
) {

    private val cacheDirectory = context.filesDir

    fun isCached(documentModel: DocumentModel): Boolean
            = findCache("${documentModel.uuid}.cache").exists()

    fun loadFromCache(documentModel: DocumentModel): Single<String> {
        return Single.create { emitter ->
            val file = findCache("${documentModel.uuid}.cache")
            if (file.exists()) {
                val text = file.inputStream().bufferedReader().use(BufferedReader::readText)
                emitter.onSuccess(text)
            } else {
                emitter.onError(FileNotFoundException(documentModel.path))
            }
        }
    }

    fun loadUndoStack(documentModel: DocumentModel): Single<UndoStack> {
        return Single.create { emitter ->
            val undoStack = try {
                restoreUndoStack(documentModel.uuid)
            } catch (e: NumberFormatException) {
                UndoStack()
            }
            emitter.onSuccess(undoStack)
        }
    }

    fun loadRedoStack(documentModel: DocumentModel): Single<UndoStack> {
        return Single.create { emitter ->
            val redoStack = try {
                restoreRedoStack(documentModel.uuid)
            } catch (e: NumberFormatException) {
                UndoStack()
            }
            emitter.onSuccess(redoStack)
        }
    }

    fun saveToCache(documentModel: DocumentModel, text: String): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val textFile = findCache("${documentModel.uuid}.cache")
            val textWriter = textFile.outputStream().bufferedWriter()
            textWriter.write(text)
            textWriter.close()

            Completable
                .fromAction {
                    appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) //Save to Database
                }
        } catch (e: IOException) {
            Completable.error(e)
        }
    }

    fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack): Completable {
        return try {
            createCacheFilesIfNecessary(documentModel)

            val undoCache = encodeUndoStack(undoStack)
            val undoFile = findCache("${documentModel.uuid}-undo.cache")
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
            val redoFile = findCache("${documentModel.uuid}-redo.cache")
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
            val textCacheFile = findCache("${documentModel.uuid}.cache")
            val undoCacheFile = findCache("${documentModel.uuid}-undo.cache")
            val redoCacheFile = findCache("${documentModel.uuid}-redo.cache")

            if (textCacheFile.exists()) { textCacheFile.delete() } //Delete text cache
            if (undoCacheFile.exists()) { undoCacheFile.delete() } //Delete undo-stack cache
            if (redoCacheFile.exists()) { redoCacheFile.delete() } //Delete redo-stack cache

            Completable
                .fromAction {
                    appDatabase.documentDao().delete(DocumentConverter.toEntity(documentModel)) //Delete from Database
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

    private fun restoreUndoStack(uuid: String): UndoStack {
        val file = findCache("$uuid-undo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStack()
    }

    private fun restoreRedoStack(uuid: String): UndoStack {
        val file = findCache("$uuid-redo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStack()
    }

    private fun readUndoStackCache(file: File): UndoStack {
        val text = file.inputStream().bufferedReader().use(BufferedReader::readText)
        return decodeUndoStack(text)
    }

    private fun encodeUndoStack(stack: UndoStack): String {
        val builder = StringBuilder()
        val delimiter = "\u0005"
        for (i in stack.count() - 1 downTo 0) {
            val (newText, oldText, start) = stack.getItemAt(i)
            builder.append(oldText)
            builder.append(delimiter)
            builder.append(newText)
            builder.append(delimiter)
            builder.append(start)
            builder.append(delimiter)
        }
        if (builder.isNotEmpty()) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }

    private fun decodeUndoStack(raw: String?): UndoStack {
        val result = UndoStack()
        if (!(raw == null || raw.isEmpty())) {
            val items = raw.split("\u0005").toTypedArray()
            if (items[items.size - 1].endsWith("\n")) {
                val item = items[items.size - 1]
                items[items.size - 1] = item.substring(0, item.length - 1)
            }
            var i = items.size - 3
            while (i >= 0) {
                val change = UndoStack.TextChange()
                change.oldText = items[i]
                change.newText = items[i + 1]
                change.start = Integer.parseInt(items[i + 2])
                result.push(change)
                i -= 3
            }
        }
        return result
    }

    private fun createCacheFilesIfNecessary(documentModel: DocumentModel) {
        val textCacheFile = findCache("${documentModel.uuid}.cache")
        val undoCacheFile = findCache("${documentModel.uuid}-undo.cache")
        val redoCacheFile = findCache("${documentModel.uuid}-redo.cache")

        if (!textCacheFile.exists()) { textCacheFile.createNewFile() } //Create text cache
        if (!undoCacheFile.exists()) { undoCacheFile.createNewFile() } //Create undo-stack cache
        if (!redoCacheFile.exists()) { redoCacheFile.createNewFile() } //Create redo-stack cache
    }

    private fun findCache(fileName: String): File = File(cacheDirectory, fileName)
}