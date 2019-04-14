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
import com.lightteam.modpeide.domain.model.DocumentModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class CacheHandler(private val context: Context) {

    fun isCached(documentModel: DocumentModel): Boolean
            = openCache("${documentModel.uuid}.cache").exists()

    fun loadFromCache(documentModel: DocumentModel): String {
        val file = openCache("${documentModel.uuid}.cache")
        val text = StringBuilder()
        file.forEachLine {
            text.append(it + '\n')
        }
        return text.toString()
    }

    fun saveToCache(documentModel: DocumentModel,
                    //undoStack: UndoStack,
                    //redoStack: UndoStack,
                    text: String) {
        createCacheFilesIfNecessary(documentModel)

        val textFile = openCache("${documentModel.uuid}.cache")
        val textOutputStreamWriter = OutputStreamWriter(FileOutputStream(textFile))
        textOutputStreamWriter.write(text)
        textOutputStreamWriter.close()

        /*val undoCache = encodeUndoStack(undoStack)
        val undoFile = openCache("${documentModel.uuid}-undo.cache")
        val outputStreamWriter = OutputStreamWriter(FileOutputStream(undoFile))
        outputStreamWriter.write(undoCache)
        outputStreamWriter.close()

        val redoCache = encodeUndoStack(redoStack)
        val redoFile = openCache("${documentModel.uuid}-redo.cache")
        val redoOutputStreamWriter = OutputStreamWriter(FileOutputStream(redoFile))
        redoOutputStreamWriter.write(redoCache)
        redoOutputStreamWriter.close()*/
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

    private fun getExternalCacheDirectory(): File = context.filesDir

    private fun createCacheFilesIfNecessary(documentModel: DocumentModel) {
        val textCacheFile = openCache("${documentModel.uuid}.cache")
        val undoCacheFile = openCache("${documentModel.uuid}-undo.cache")
        val redoCacheFile = openCache("${documentModel.uuid}-redo.cache")

        if (!textCacheFile.exists()) { textCacheFile.createNewFile() } //Create text cache
        if (!undoCacheFile.exists()) { undoCacheFile.createNewFile() } //Create undo-stack cache
        if (!redoCacheFile.exists()) { redoCacheFile.createNewFile() } //Create redo-stack cache
    }

    private fun openCache(fileName: String): File = File(getExternalCacheDirectory(), fileName)
}