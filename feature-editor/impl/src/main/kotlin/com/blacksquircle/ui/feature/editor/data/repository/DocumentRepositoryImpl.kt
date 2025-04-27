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

package com.blacksquircle.ui.feature.editor.data.repository

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blacksquircle.ui.core.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.extensions.PermissionException
import com.blacksquircle.ui.core.extensions.isStorageAccessGranted
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.data.manager.CacheManager
import com.blacksquircle.ui.feature.editor.data.mapper.DocumentMapper
import com.blacksquircle.ui.feature.editor.data.utils.charsetFor
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.view.selectionEnd
import com.blacksquircle.ui.feature.editor.ui.editor.view.selectionStart
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.LineBreak
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.saf.SAFFilesystem
import io.github.rosemoe.sora.text.Content
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val cacheManager: CacheManager,
    private val documentDao: DocumentDao,
    private val filesystemFactory: FilesystemFactory,
    private val context: Context,
) : DocumentRepository {

    override suspend fun loadDocuments(): List<DocumentModel> {
        return withContext(dispatcherProvider.io()) {
            documentDao.loadAll().map(DocumentMapper::toModel)
        }
    }

    override suspend fun loadDocument(document: DocumentModel): Content {
        return withContext(dispatcherProvider.io()) {
            val documentEntity = DocumentMapper.toEntity(document)
            documentDao.insert(documentEntity)
            settingsManager.selectedUuid = document.uuid

            if (cacheManager.isCached(document)) {
                cacheManager.loadContent(document)
            } else {
                if (!context.isStorageAccessGranted()) {
                    throw PermissionException()
                }
                val filesystem = filesystemFactory.create(document.filesystemUuid)
                val fileModel = DocumentMapper.toModel(document)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening),
                )
                Content(filesystem.loadFile(fileModel, fileParams)).also { content ->
                    cacheDocument(document, content)
                }
            }
        }
    }

    override suspend fun saveDocument(document: DocumentModel, content: Content) {
        withContext(dispatcherProvider.io()) {
            val filesystem = filesystemFactory.create(document.filesystemUuid)
            val fileModel = DocumentMapper.toModel(document)
            val fileParams = FileParams(
                charset = charsetFor(settingsManager.encodingForSaving),
                linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
            )
            filesystem.saveFile(fileModel, content.toString(), fileParams).also {
                cacheDocument(document, content)
            }
        }
    }

    override suspend fun cacheDocument(document: DocumentModel, content: Content) {
        withContext(dispatcherProvider.io()) {
            cacheManager.create(document)
            cacheManager.saveContent(document, content)

            documentDao.updateProperties(
                uuid = document.uuid,
                modified = document.modified,
                scrollX = content.scrollX,
                scrollY = content.scrollY,
                selectionStart = content.selectionStart,
                selectionEnd = content.selectionEnd
            )
        }
    }

    override suspend fun refreshDocument(document: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            cacheManager.delete(document)
            documentDao.updateProperties(
                uuid = document.uuid,
                modified = false,
                scrollX = 0,
                scrollY = 0,
                selectionStart = 0,
                selectionEnd = 0,
            )
        }
    }

    override suspend fun reorderDocuments(from: DocumentModel, to: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.reorderDocuments(
                fromUuid = from.uuid,
                toUuid = to.uuid,
                fromIndex = from.position,
                toIndex = to.position,
            )
        }
    }

    override suspend fun closeDocument(document: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.closeDocument(document.uuid, document.position)
            cacheManager.delete(document)
        }
    }

    override suspend fun closeOtherDocuments(document: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            documentDao.closeOtherDocuments(document.uuid)
            settingsManager.selectedUuid = document.uuid
            cacheManager.deleteAll { file ->
                !file.name.startsWith(document.uuid, ignoreCase = true)
            }
        }
    }

    override suspend fun closeAllDocuments() {
        withContext(dispatcherProvider.io()) {
            documentDao.deleteAll()
            cacheManager.deleteAll()
            settingsManager.selectedUuid = ""
        }
    }

    override suspend fun changeModified(document: DocumentModel, modified: Boolean) {
        withContext(dispatcherProvider.io()) {
            documentDao.updateModified(document.uuid, modified)
        }
    }

    override suspend fun changeLanguage(document: DocumentModel, language: String) {
        withContext(dispatcherProvider.io()) {
            documentDao.updateLanguage(document.uuid, language)
        }
    }

    override suspend fun openExternal(fileUri: Uri, position: Int): DocumentModel {
        return withContext(dispatcherProvider.io()) {
            val fileModel = when {
                DocumentsContract.isDocumentUri(context, fileUri) -> {
                    try {
                        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(fileUri, flags)
                    } catch (e: SecurityException) {
                        Timber.e(e, e.message)
                    }
                    FileModel(
                        fileUri = fileUri.toString(),
                        filesystemUuid = SAFFilesystem.SAF_UUID,
                    )
                }
                fileUri.scheme == ContentResolver.SCHEME_CONTENT -> {
                    val filePath = context.contentResolver.query(
                        /* uri = */ fileUri,
                        /* projection = */ arrayOf(MediaStore.Files.FileColumns.DATA),
                        /* selection = */ null,
                        /* selectionArgs = */ null,
                        /* sortOrder = */ null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                            if (columnIndex != -1) {
                                return@use cursor.getString(columnIndex)
                            }
                        }
                        return@use null
                    }
                    if (filePath == null) {
                        FileModel(
                            fileUri = fileUri.toString(),
                            filesystemUuid = SAFFilesystem.SAF_UUID,
                        )
                    } else {
                        FileModel(
                            fileUri = LocalFilesystem.LOCAL_SCHEME + filePath,
                            filesystemUuid = LocalFilesystem.LOCAL_UUID,
                        )
                    }
                }
                fileUri.scheme == ContentResolver.SCHEME_FILE -> {
                    FileModel(
                        fileUri = fileUri.toString(),
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    )
                }
                else -> throw IllegalArgumentException("File $fileUri not found")
            }

            DocumentMapper.toModel(fileModel, position = position)
        }
    }

    override suspend fun saveExternal(document: DocumentModel, content: Content, fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            val filesystemUuid = SAFFilesystem.SAF_UUID
            val filesystem = filesystemFactory.create(filesystemUuid)
            val fileModel = DocumentMapper.toModel(
                document.copy(
                    fileUri = fileUri.toString(),
                    filesystemUuid = filesystemUuid,
                )
            )
            val fileParams = FileParams(
                charset = charsetFor(settingsManager.encodingForSaving),
                linebreak = LineBreak.of(settingsManager.lineBreakForSaving),
            )
            filesystem.saveFile(fileModel, content.toString(), fileParams)
        }
    }
}