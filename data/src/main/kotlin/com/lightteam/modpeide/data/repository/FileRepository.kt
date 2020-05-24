/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.repository

import com.lightteam.editorkit.feature.undoredo.UndoStack
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.delegate.LanguageDelegate
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.safeCharset
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.model.editor.DocumentContent
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.domain.repository.DocumentRepository
import io.reactivex.Completable
import io.reactivex.Single

class FileRepository(
    private val preferenceHandler: PreferenceHandler,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val fileModel = DocumentConverter.toModel(documentModel)
        val charset = safeCharset(preferenceHandler.getEncodingForOpening().get())
        return filesystem.loadFile(fileModel, charset)
            .map { text ->
                appDatabase.documentDao().insert(DocumentConverter.toEntity(documentModel)) // Save to Database

                val language = LanguageDelegate.provideLanguage(documentModel.name)
                val undoStack = UndoStack()
                val redoStack = UndoStack()

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
        val charset = safeCharset(preferenceHandler.getEncodingForSaving().get())
        return filesystem.saveFile(fileModel, text, charset)
            .doOnComplete {
                appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) // Save to Database
            }
    }
}