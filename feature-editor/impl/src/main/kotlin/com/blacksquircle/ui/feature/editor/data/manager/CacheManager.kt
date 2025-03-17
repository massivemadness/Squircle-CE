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

package com.blacksquircle.ui.feature.editor.data.manager

import com.blacksquircle.ui.feature.editor.data.utils.readFile
import com.blacksquircle.ui.feature.editor.data.utils.writeFile
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.UndoManager
import java.io.File

internal class CacheManager(private val cacheDir: File) {

    fun isCached(document: DocumentModel): Boolean {
        return cacheFile(document, postfix = TEXT).exists()
    }

    fun saveText(document: DocumentModel, content: Content) {
        val textFile = cacheFile(document, postfix = TEXT)
        if (!textFile.exists()) {
            textFile.createNewFile()
        }
        textFile.writeText(content.toString())
    }

    fun loadText(document: DocumentModel): String {
        val textFile = cacheFile(document, postfix = TEXT)
        if (!textFile.exists()) {
            textFile.createNewFile()
        }
        return textFile.readText()
    }

    fun saveUndoHistory(document: DocumentModel, undoManager: UndoManager) {
        val historyFile = cacheFile(document, postfix = HISTORY)
        if (!historyFile.exists()) {
            historyFile.createNewFile()
        }
        undoManager.writeFile(historyFile)
    }

    fun loadUndoHistory(document: DocumentModel): UndoManager? {
        val undoCacheFile = cacheFile(document, postfix = HISTORY)
        if (!undoCacheFile.exists()) {
            return null
        }
        return UndoManager.CREATOR.readFile(undoCacheFile)
    }

    fun create(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = TEXT)
        val undoCacheFile = cacheFile(document, postfix = HISTORY)

        if (!textCacheFile.exists()) textCacheFile.createNewFile()
        if (!undoCacheFile.exists()) undoCacheFile.createNewFile()
    }

    fun delete(document: DocumentModel) {
        val textCacheFile = cacheFile(document, postfix = TEXT)
        val undoCacheFile = cacheFile(document, postfix = HISTORY)

        if (textCacheFile.exists()) textCacheFile.delete()
        if (undoCacheFile.exists()) undoCacheFile.delete()
    }

    fun deleteAll(predicate: (File) -> Boolean) {
        cacheDir.listFiles().orEmpty().forEach { file ->
            if (predicate(file)) {
                file.deleteRecursively()
            }
        }
    }

    fun deleteAll() {
        cacheDir.listFiles().orEmpty().forEach { file ->
            file.deleteRecursively()
        }
    }

    private fun cacheFile(document: DocumentModel, postfix: String): File {
        return File(cacheDir, "${document.uuid}-$postfix")
    }

    companion object {
        private const val TEXT = "text.txt"
        private const val HISTORY = "history.txt"
    }
}