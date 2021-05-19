/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.domain.repository.documents

import com.blacksquircle.ui.domain.model.documents.DocumentParams
import com.blacksquircle.ui.domain.model.editor.DocumentContent
import com.blacksquircle.ui.domain.model.editor.DocumentModel

interface DocumentRepository {

    suspend fun fetchDocuments(): List<DocumentModel>
    suspend fun updateDocument(documentModel: DocumentModel)
    suspend fun deleteDocument(documentModel: DocumentModel)

    suspend fun loadFile(documentModel: DocumentModel): DocumentContent
    suspend fun saveFile(content: DocumentContent, params: DocumentParams)
}