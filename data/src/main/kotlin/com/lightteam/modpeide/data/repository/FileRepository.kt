package com.lightteam.modpeide.data.repository

import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.feature.language.LanguageProvider
import com.lightteam.modpeide.data.feature.undoredo.UndoStackImpl
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.domain.editor.DocumentContent
import com.lightteam.modpeide.domain.editor.DocumentModel
import com.lightteam.modpeide.domain.repository.DocumentRepository
import io.reactivex.Completable
import io.reactivex.Single

class FileRepository(
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val fileModel = DocumentConverter.toModel(documentModel)
        return filesystem.loadFile(fileModel)
            .map { text ->
                appDatabase.documentDao().insert(DocumentConverter.toEntity(documentModel)) // Save to Database

                val language = LanguageProvider.provide(documentModel)
                val undoStack = UndoStackImpl()
                val redoStack = UndoStackImpl()

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
        val fileModel = DocumentConverter.toModel(documentModel)
        return filesystem.saveFile(fileModel, text)
            .doOnComplete {
                appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) // Save to Database
            }
    }
}