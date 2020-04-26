package com.lightteam.modpeide.domain.repository

import com.lightteam.modpeide.domain.editor.DocumentContent
import com.lightteam.modpeide.domain.editor.DocumentModel
import io.reactivex.Completable
import io.reactivex.Single

interface DocumentRepository {
    fun loadFile(documentModel: DocumentModel): Single<DocumentContent>
    fun saveFile(documentModel: DocumentModel, text: String): Completable
}