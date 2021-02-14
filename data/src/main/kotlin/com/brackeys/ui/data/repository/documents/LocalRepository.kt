/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.data.repository.documents

import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.delegate.LanguageDelegate
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.safeCharset
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.domain.repository.documents.DocumentRepository
import com.brackeys.ui.editorkit.utils.UndoStack
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileParams
import com.brackeys.ui.filesystem.base.model.LineBreak
import io.reactivex.Completable
import io.reactivex.Single
import java.nio.charset.Charset

class LocalRepository(
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem
) : DocumentRepository {

    private val encodingAutoDetect: Boolean
        get() = settingsManager.getEncodingAutoDetect().get()
    private val encodingForOpening: Charset
        get() = safeCharset(settingsManager.getEncodingForOpening().get())
    private val encodingForSaving: Charset
        get() = safeCharset(settingsManager.getEncodingForSaving().get())

    private val linebreakForSaving: LineBreak
        get() = LineBreak.find(settingsManager.getLinebreakForSaving().get())

    override fun loadFile(documentModel: DocumentModel): Single<DocumentContent> {
        val fileModel = DocumentConverter.toModel(documentModel)
        val fileParams = FileParams(chardet = encodingAutoDetect, charset = encodingForOpening)
        val documentEntity = DocumentConverter.toEntity(documentModel)

        return filesystem.loadFile(fileModel, fileParams)
            .map { text ->
                appDatabase.documentDao().insert(documentEntity)

                DocumentContent(
                    documentModel = documentModel,
                    language = LanguageDelegate.provideLanguage(documentModel.name),
                    undoStack = UndoStack(),
                    redoStack = UndoStack(),
                    text = text
                )
            }
    }

    override fun saveFile(documentContent: DocumentContent): Completable {
        val documentModel = documentContent.documentModel
        val text = documentContent.text

        val fileModel = DocumentConverter.toModel(documentModel)
        val fileParams = FileParams(charset = encodingForSaving, linebreak = linebreakForSaving)
        val documentEntity = DocumentConverter.toEntity(documentModel)

        return filesystem.saveFile(fileModel, text, fileParams)
            .doOnComplete { appDatabase.documentDao().update(documentEntity) }
    }
}