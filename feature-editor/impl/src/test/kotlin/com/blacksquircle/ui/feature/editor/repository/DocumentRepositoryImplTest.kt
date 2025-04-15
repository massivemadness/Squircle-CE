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

package com.blacksquircle.ui.feature.editor.repository

import android.content.Context
import com.blacksquircle.ui.core.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.isStorageAccessGranted
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.createDocumentEntity
import com.blacksquircle.ui.feature.editor.data.manager.CacheManager
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import com.blacksquircle.ui.feature.editor.data.repository.DocumentRepositoryImpl
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.github.rosemoe.sora.text.Content
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DocumentRepositoryImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val cacheManager = mockk<CacheManager>(relaxed = true)
    private val documentDao = mockk<DocumentDao>(relaxed = true)
    private val filesystemFactory = mockk<FilesystemFactory>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val filesystem = mockk<Filesystem>(relaxed = true)

    private val documentRepository = DocumentRepositoryImpl(
        dispatcherProvider = dispatcherProvider,
        settingsManager = settingsManager,
        cacheManager = cacheManager,
        documentDao = documentDao,
        filesystemFactory = filesystemFactory,
        context = context
    )

    @Before
    fun setup() {
        coEvery { filesystemFactory.create(any()) } returns filesystem

        every { settingsManager.encodingForOpening } returns Charsets.UTF_8.name()
        every { settingsManager.encodingForSaving } returns Charsets.UTF_8.name()
        every { settingsManager.lineBreakForSaving } returns LineBreak.LF.value
    }

    @Test
    fun `When loading documents Then load from database`() = runTest {
        // Given
        val documentModel = createDocument("12345", "file.txt")
        val documentEntity = createDocumentEntity("12345", "file.txt")
        coEvery { documentDao.loadAll() } returns listOf(documentEntity)

        // When
        val documentList = documentRepository.loadDocuments()

        // Then
        assertEquals(listOf(documentModel), documentList)
    }

    @Test
    fun `When document is cached Then load content from cache`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        every { cacheManager.isCached(document) } returns true
        every { cacheManager.loadContent(document) } returns Content()

        // When
        documentRepository.loadDocument(document)

        // Then
        verify(exactly = 1) { cacheManager.loadContent(document) }
        verify(exactly = 1) { settingsManager.selectedUuid = document.uuid }
    }

    @Test
    fun `When document is not cached Then load content from filesystem`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        every { cacheManager.isCached(document) } returns false

        mockkStatic(Context::isStorageAccessGranted)
        every { context.isStorageAccessGranted() } returns true

        // When
        documentRepository.loadDocument(document)

        // Then
        val fileModel = FileModel(
            fileUri = document.fileUri,
            filesystemUuid = document.filesystemUuid,
        )
        verify(exactly = 1) { filesystem.loadFile(fileModel, any()) }
        verify(exactly = 1) { settingsManager.selectedUuid = document.uuid }
    }

    @Test(expected = PermissionException::class)
    fun `When loading content without permission Then throw PermissionException`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        every { cacheManager.isCached(document) } returns false

        mockkStatic(Context::isStorageAccessGranted)
        every { context.isStorageAccessGranted() } returns false

        // When
        documentRepository.loadDocument(document)

        // Then - throws exception
    }

    @Test
    fun `When document is loaded Then save content to cache`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        every { cacheManager.isCached(document) } returns false

        mockkStatic(Context::isStorageAccessGranted)
        every { context.isStorageAccessGranted() } returns true

        // When
        documentRepository.loadDocument(document)

        // Then
        verify(exactly = 1) { cacheManager.create(document) }
        verify(exactly = 1) { cacheManager.saveContent(document, any()) }
        coVerify(exactly = 1) {
            documentDao.updateProperties(document.uuid, any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `When saving document Then save content to filesystem`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        val content = Content("Hello World!")

        // When
        documentRepository.saveDocument(document, content)

        // Then
        val fileModel = FileModel(
            fileUri = document.fileUri,
            filesystemUuid = document.filesystemUuid,
        )
        verify(exactly = 1) { filesystem.saveFile(fileModel, content.toString(), any()) }
    }

    @Test
    fun `When document is saved Then save content to cache`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        val content = Content("Hello World!")

        // When
        documentRepository.saveDocument(document, content)

        // Then
        verify(exactly = 1) { cacheManager.create(document) }
        verify(exactly = 1) { cacheManager.saveContent(document, content) }
        coVerify(exactly = 1) {
            documentDao.updateProperties(document.uuid, any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `When caching document Then update cache`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")
        val content = Content("Hello World!")

        // When
        documentRepository.cacheDocument(document, content)

        // Then
        verify(exactly = 1) { cacheManager.create(document) }
        verify(exactly = 1) { cacheManager.saveContent(document, content) }
        coVerify(exactly = 1) {
            documentDao.updateProperties(document.uuid, any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `When reordering documents Then reorder in database`() = runTest {
        // Given
        val first = createDocument(uuid = "1", fileName = "file1.txt", position = 0)
        val second = createDocument(uuid = "2", fileName = "file2.txt", position = 1)

        // When
        documentRepository.reorderDocuments(first, second)

        // Then
        coVerify(exactly = 1) {
            documentDao.reorderDocuments(
                fromUuid = first.uuid,
                fromIndex = first.position,
                toUuid = second.uuid,
                toIndex = second.position,
            )
        }
    }

    @Test
    fun `When closing document Then delete from database and clear cache`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")

        // When
        documentRepository.closeDocument(document)

        // Then
        verify(exactly = 1) { cacheManager.delete(document) }
        coVerify(exactly = 1) {
            documentDao.closeDocument(document.uuid, document.position)
        }
    }

    @Test
    fun `When closing other documents Then close other documents`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")

        // When
        documentRepository.closeOtherDocuments(document)

        // Then
        verify(exactly = 1) { cacheManager.deleteAll(any()) }
        verify(exactly = 1) { settingsManager.selectedUuid = document.uuid }
        coVerify(exactly = 1) { documentDao.closeOtherDocuments(document.uuid) }
    }

    @Test
    fun `When closing all documents Then delete all from database and clear cache`() = runTest {
        // When
        documentRepository.closeAllDocuments()

        // Then
        verify(exactly = 1) { settingsManager.selectedUuid = any() }
        verify(exactly = 1) { cacheManager.deleteAll() }
        coVerify(exactly = 1) { documentDao.deleteAll() }
    }

    @Test
    fun `When document is changed Then update modified flag in database`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")

        // When
        documentRepository.changeModified(document, true)

        // Then
        coVerify(exactly = 1) { documentDao.updateModified(document.uuid, true) }
    }

    @Test
    fun `When language is changed Then update language in database`() = runTest {
        // Given
        val document = createDocument("12345", "file.txt")

        // When
        documentRepository.changeLanguage(document, LanguageScope.C)

        // Then
        coVerify(exactly = 1) { documentDao.updateLanguage(document.uuid, LanguageScope.C) }
    }
}