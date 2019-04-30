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
import com.lightteam.modpeide.data.storage.collection.UndoStack
import com.lightteam.modpeide.domain.model.DocumentModel
import java.io.*

class CacheHandler(private val context: Context) {

    fun isCached(documentModel: DocumentModel): Boolean
            = openCache("${documentModel.uuid}.cache").exists()

    fun loadFromCache(documentModel: DocumentModel): String {
        val file = openCache("${documentModel.uuid}.cache")
        return file.inputStream().bufferedReader().use(BufferedReader::readText)
    }

    fun saveToCache(documentModel: DocumentModel, text: String) {
        createCacheFilesIfNecessary(documentModel)

        val textFile = openCache("${documentModel.uuid}.cache")
        val textWriter = textFile.outputStream().bufferedWriter()
        textWriter.write(text)
        textWriter.close()
    }

    fun saveUndoStacks(documentModel: DocumentModel, stacks: Pair<UndoStack, UndoStack>) {
        val undoStack = stacks.first
        val redoStack = stacks.second

        val undoCache = encodeUndoStack(undoStack)
        val undoFile = openCache("${documentModel.uuid}-undo.cache")
        val undoWriter = undoFile.outputStream().bufferedWriter()
        undoWriter.write(undoCache)
        undoWriter.close()

        val redoCache = encodeUndoStack(redoStack)
        val redoFile = openCache("${documentModel.uuid}-redo.cache")
        val redoWriter = redoFile.outputStream().bufferedWriter()
        redoWriter.write(redoCache)
        redoWriter.close()
    }

    fun loadUndoStacks(documentModel: DocumentModel): Pair<UndoStack, UndoStack> {
        return Pair(
            restoreUndoStack(documentModel.uuid),
            restoreRedoStack(documentModel.uuid)
        )
    }

    fun invalidateCache(documentModel: DocumentModel) {
        val textCacheFile = openCache("${documentModel.uuid}.cache")
        val undoCacheFile = openCache("${documentModel.uuid}-undo.cache")
        val redoCacheFile = openCache("${documentModel.uuid}-redo.cache")

        if (textCacheFile.exists()) { textCacheFile.delete() } //Delete text cache
        if (undoCacheFile.exists()) { undoCacheFile.delete() } //Delete undo-stack cache
        if (redoCacheFile.exists()) { redoCacheFile.delete() } //Delete redo-stack cache
    }

    fun invalidateCaches() {
        getExternalCacheDirectory().listFiles().forEach {
            it.deleteRecursively()
        }
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

    private fun restoreUndoStack(uuid: String): UndoStack {
        val file = openCache("$uuid-undo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStack()
    }

    private fun restoreRedoStack(uuid: String): UndoStack {
        val file = openCache("$uuid-redo.cache")
        if (file.exists()) {
            return readUndoStackCache(file)
        }
        return UndoStack()
    }

    private fun readUndoStackCache(file: File): UndoStack {
        val text = file.inputStream().bufferedReader().use(BufferedReader::readText)
        return decodeUndoStack(text)
    }

    private fun getExternalCacheDirectory(): File = context.filesDir

    private fun createCacheFilesIfNecessary(documentModel: DocumentModel) {
        val textCacheFile = openCache("${documentModel.uuid}.cache")
        val undoCacheFile = openCache("${documentModel.uuid}-undo.cache")
        val redoCacheFile = openCache("${documentModel.uuid}-redo.cache")

        if (!textCacheFile.exists()) { textCacheFile.createNewFile() } //Create text cache
        if (!undoCacheFile.exists()) { undoCacheFile.createNewFile() } //Create undo-stack cache
        if (!redoCacheFile.exists()) { redoCacheFile.createNewFile() } //Create redo-stack cache
    }

    private fun openCache(fileName: String): File
            = File(getExternalCacheDirectory(), fileName)
}