package com.brackeys.ui.utils.extensions

import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.filesystem.base.model.FileModel

fun Collection<FileModel>.containsFileModel(fileModel: FileModel): Boolean {
    forEach { indexedModel ->
        if (indexedModel.path == fileModel.path) {
            return true
        }
    }
    return false
}

fun Collection<DocumentModel>.containsDocumentModel(documentModel: DocumentModel): Boolean {
    forEach { indexedModel ->
        if (indexedModel.path == documentModel.path) {
            return true
        }
    }
    return false
}

fun Collection<DocumentModel>.indexBy(uuid: String): Int? {
    forEachIndexed { index, indexedModel ->
        if (indexedModel.uuid == uuid) {
            return index
        }
    }
    return null
}

fun Collection<DocumentModel>.indexBy(documentModel: DocumentModel): Int? {
    forEachIndexed { index, indexedModel ->
        if (indexedModel.path == documentModel.path) {
            return index
        }
    }
    return null
}

fun <T> MutableList<T>.replaceList(collection: Collection<T>) {
    clear()
    addAll(collection)
}