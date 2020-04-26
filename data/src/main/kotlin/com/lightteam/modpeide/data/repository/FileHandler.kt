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

class FileHandler(
    private val filesystem: Filesystem,
    private val appDatabase: AppDatabase
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        return filesystem.loadFile(DocumentConverter.toModel(documentModel))
            .map { text ->
                appDatabase.documentDao().insert(DocumentConverter.toEntity(documentModel)) // Save to Database

                return@map DocumentContent(
                    documentModel,
                    LanguageProvider.provide(documentModel),
                    UndoStackImpl(),
                    UndoStackImpl(),
                    text
                )
            }
    }

    override fun saveFile(documentModel: DocumentModel, text: String): Completable {
        return filesystem.saveFile(DocumentConverter.toModel(documentModel), text)
            .andThen {
                appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) // Save to Database
            }
    }
}