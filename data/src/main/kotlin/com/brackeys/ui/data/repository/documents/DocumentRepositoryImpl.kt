package com.brackeys.ui.data.repository.documents

import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.delegate.LanguageDelegate
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.charsetFor
import com.brackeys.ui.data.utils.decodeStack
import com.brackeys.ui.data.utils.encodeStack
import com.brackeys.ui.domain.model.documents.DocumentParams
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.domain.providers.coroutines.DispatcherProvider
import com.brackeys.ui.domain.repository.documents.DocumentRepository
import com.brackeys.ui.editorkit.utils.UndoStack
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileParams
import com.brackeys.ui.filesystem.base.model.LineBreak
import kotlinx.coroutines.withContext

class DocumentRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val localFilesystem: Filesystem,
    private val cacheFilesystem: Filesystem
) : DocumentRepository {

    override suspend fun fetchDocuments(): List<DocumentModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.documentDao().loadAll()
                .map(DocumentConverter::toModel)
        }
    }

    override suspend fun updateDocument(documentModel: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            val documentEntity = DocumentConverter.toEntity(documentModel)
            appDatabase.documentDao().insert(documentEntity)
        }
    }

    override suspend fun deleteDocument(documentModel: DocumentModel) {
        withContext(dispatcherProvider.io()) {
            deleteCacheFiles(documentModel)

            val documentEntity = DocumentConverter.toEntity(documentModel)
            appDatabase.documentDao().delete(documentEntity)
        }
    }

    override suspend fun loadFile(documentModel: DocumentModel): DocumentContent {
        return withContext(dispatcherProvider.io()) {
            val cacheFile = cacheFile(documentModel, postfix = "text")
            if (cacheFilesystem.isExists(cacheFile)) {
                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageDelegate.provideLanguage(documentModel.name),
                    undoStack = loadUndoStack(documentModel),
                    redoStack = loadRedoStack(documentModel),
                    text = cacheFilesystem.loadFile(cacheFile, FileParams())
                )
            } else {
                updateDocument(documentModel)

                val fileModel = DocumentConverter.toModel(documentModel)
                val fileParams = FileParams(
                    chardet = settingsManager.encodingAutoDetect,
                    charset = charsetFor(settingsManager.encodingForOpening)
                )

                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageDelegate.provideLanguage(documentModel.name),
                    undoStack = UndoStack(),
                    redoStack = UndoStack(),
                    text = localFilesystem.loadFile(fileModel, fileParams)
                )
            }
        }
    }

    override suspend fun saveFile(content: DocumentContent, params: DocumentParams) {
        withContext(dispatcherProvider.io()) {
            if (params.local) {
                val fileModel = DocumentConverter.toModel(content.documentModel)
                val fileParams = FileParams(
                    charset = charsetFor(settingsManager.encodingForSaving),
                    linebreak = LineBreak.find(settingsManager.lineBreakForSaving)
                )
                localFilesystem.saveFile(fileModel, content.text, fileParams)
            }
            if (params.cache) {
                createCacheFiles(content.documentModel)

                val textCacheFile = cacheFile(content.documentModel, postfix = "text")
                cacheFilesystem.saveFile(textCacheFile, content.text, FileParams())

                val undoCacheFile = cacheFile(content.documentModel, postfix = "undo")
                val undoStackText = content.undoStack.encodeStack()
                cacheFilesystem.saveFile(undoCacheFile, undoStackText, FileParams())

                val redoCacheFile = cacheFile(content.documentModel, postfix = "redo")
                val redoStackText = content.redoStack.encodeStack()
                cacheFilesystem.saveFile(redoCacheFile, redoStackText, FileParams())

                updateDocument(content.documentModel)
            }
        }
    }

    private suspend fun loadUndoStack(documentModel: DocumentModel): UndoStack {
        return try {
            val undoCacheFile = cacheFile(documentModel, postfix = "undo")
            if (cacheFilesystem.isExists(undoCacheFile)) {
                return cacheFilesystem.loadFile(undoCacheFile, FileParams())
                    .decodeStack()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private suspend fun loadRedoStack(documentModel: DocumentModel): UndoStack {
        return try {
            val redoCacheFile = cacheFile(documentModel, postfix = "redo")
            if (cacheFilesystem.isExists(redoCacheFile)) {
                return cacheFilesystem.loadFile(redoCacheFile, FileParams())
                    .decodeStack()
            }
            UndoStack()
        } catch (e: Exception) {
            UndoStack()
        }
    }

    private suspend fun createCacheFiles(documentModel: DocumentModel) {
        val textCacheFile = cacheFile(documentModel, postfix = "text")
        val undoCacheFile = cacheFile(documentModel, postfix = "undo")
        val redoCacheFile = cacheFile(documentModel, postfix = "redo")

        if (!cacheFilesystem.isExists(textCacheFile)) { cacheFilesystem.createFile(textCacheFile) }
        if (!cacheFilesystem.isExists(undoCacheFile)) { cacheFilesystem.createFile(undoCacheFile) }
        if (!cacheFilesystem.isExists(redoCacheFile)) { cacheFilesystem.createFile(redoCacheFile) }
    }

    private suspend fun deleteCacheFiles(documentModel: DocumentModel) {
        val textCacheFile = cacheFile(documentModel, postfix = "text")
        val undoCacheFile = cacheFile(documentModel, postfix = "undo")
        val redoCacheFile = cacheFile(documentModel, postfix = "redo")

        if (cacheFilesystem.isExists(textCacheFile)) { cacheFilesystem.deleteFile(textCacheFile) }
        if (cacheFilesystem.isExists(undoCacheFile)) { cacheFilesystem.deleteFile(undoCacheFile) }
        if (cacheFilesystem.isExists(redoCacheFile)) { cacheFilesystem.deleteFile(redoCacheFile) }
    }

    private suspend fun cacheFile(documentModel: DocumentModel, postfix: String) = FileModel(
        name = "${documentModel.uuid}-$postfix",
        path = cacheFilesystem.defaultLocation().path + "/" +
            "${documentModel.uuid}-$postfix.cache"
    )
}