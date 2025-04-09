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

package com.blacksquircle.ui.feature.editor.manager

import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.data.manager.CacheManager
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class CacheManagerTest {

    private lateinit var cacheDir: File
    private lateinit var cacheManager: CacheManager

    private val document = createDocument(uuid = "123", fileName = "file.txt")

    @Before
    fun setup() {
        cacheDir = Files.createTempDirectory("cacheTest").toFile()
        cacheManager = CacheManager(cacheDir)
    }

    @After
    fun cleanup() {
        cacheDir.deleteRecursively()
    }

    @Test
    fun `When file exists Then isCached returns true`() {
        // Given
        val textFile = File(cacheDir, "${document.uuid}-${CacheManager.TEXT}")
        textFile.createNewFile()

        // When
        val result = cacheManager.isCached(document)

        // Then
        assertTrue(result)
    }

    @Test
    fun `When file does not exists Then isCached returns false`() {
        // When
        val result = cacheManager.isCached(document)

        // Then
        assertFalse(result)
    }

    @Test
    fun `When document is cached Then cache files are created`() {
        // Given
        val textFile = File(cacheDir, "${document.uuid}-${CacheManager.TEXT}")
        val historyFile = File(cacheDir, "${document.uuid}-${CacheManager.HISTORY}")

        // When
        cacheManager.create(document)

        // Then
        assertTrue(textFile.exists())
        assertTrue(historyFile.exists())
    }

    @Test
    fun `When document is deleted Then cache files are deleted`() {
        // Given
        val textFile = File(cacheDir, "${document.uuid}-${CacheManager.TEXT}")
        val historyFile = File(cacheDir, "${document.uuid}-${CacheManager.HISTORY}")

        // When
        cacheManager.delete(document)

        // Then
        assertFalse(textFile.exists())
        assertFalse(historyFile.exists())
    }
}