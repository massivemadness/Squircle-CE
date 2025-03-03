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

package com.blacksquircle.ui.feature.fonts.data.repository

import android.content.Context
import android.net.Uri
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.fonts.api.model.FontModel
import com.blacksquircle.ui.feature.fonts.api.model.InternalFont
import com.blacksquircle.ui.feature.fonts.data.mapper.FontMapper
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

internal class FontsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val context: Context,
) : FontsRepository {

    override suspend fun current(): FontModel {
        return withContext(dispatcherProvider.io()) {
            val fontUuid = settingsManager.fontType
            InternalFont.find(fontUuid) ?: loadFont(fontUuid)
        }
    }

    override suspend fun loadFonts(query: String): List<FontModel> {
        return withContext(dispatcherProvider.io()) {
            val defaultFonts = InternalFont.entries
                .map(InternalFont::font)
                .filter { it.name.contains(query, ignoreCase = true) }
            val userFonts = appDatabase.fontDao().loadAll()
                .map(FontMapper::toModel)
                .filter { it.name.contains(query, ignoreCase = true) }
            userFonts + defaultFonts
        }
    }

    override suspend fun loadFont(uuid: String): FontModel {
        return withContext(dispatcherProvider.io()) {
            val fontEntity = appDatabase.fontDao().load(uuid)
            FontMapper.toModel(fontEntity)
        }
    }

    override suspend fun importFont(fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val fontUuid = UUID.randomUUID().toString()
                val fontName = fileUri.path.orEmpty().substringAfterLast(File.separator)
                val fontFile = File(Directories.fontsDir(context), fontUuid)
                if (!fontFile.exists()) {
                    fontFile.createNewFile()
                    inputStream.copyTo(fontFile.outputStream())
                }
                val fontModel = FontModel(
                    uuid = fontUuid,
                    name = fontName,
                    path = fontFile.absolutePath,
                    isExternal = true,
                )
                appDatabase.fontDao().insert(FontMapper.toEntity(fontModel))
            }
        }
    }

    override suspend fun selectFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.fontType = fontModel.uuid
        }
    }

    override suspend fun removeFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            val fontFile = File(Directories.fontsDir(context), fontModel.uuid)
            if (fontFile.exists()) {
                fontFile.deleteRecursively()
            }
            appDatabase.fontDao().delete(fontModel.uuid)
            if (settingsManager.fontType == fontModel.uuid) {
                settingsManager.remove(SettingsManager.KEY_FONT_TYPE)
            }
        }
    }
}