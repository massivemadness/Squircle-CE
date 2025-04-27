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

package com.blacksquircle.ui.feature.editor.domain.repository

import android.net.Uri
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import io.github.rosemoe.sora.text.Content

internal interface DocumentRepository {

    suspend fun loadDocuments(): List<DocumentModel>

    suspend fun loadDocument(document: DocumentModel): Content
    suspend fun saveDocument(document: DocumentModel, content: Content)
    suspend fun cacheDocument(document: DocumentModel, content: Content)
    suspend fun refreshDocument(document: DocumentModel)

    suspend fun reorderDocuments(from: DocumentModel, to: DocumentModel)

    suspend fun closeDocument(document: DocumentModel)
    suspend fun closeOtherDocuments(document: DocumentModel)
    suspend fun closeAllDocuments()

    suspend fun changeModified(document: DocumentModel, modified: Boolean)
    suspend fun changeLanguage(document: DocumentModel, language: String)

    suspend fun openExternal(fileUri: Uri, position: Int): DocumentModel
    suspend fun saveExternal(document: DocumentModel, content: Content, fileUri: Uri)
}