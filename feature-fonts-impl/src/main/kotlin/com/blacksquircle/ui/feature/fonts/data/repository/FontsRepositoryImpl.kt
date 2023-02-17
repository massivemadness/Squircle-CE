/*
 * Copyright 2023 Squircle CE contributors.
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
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.fonts.data.converter.FontConverter
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class FontsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val context: Context,
) : FontsRepository {

    override suspend fun loadFonts(): List<FontModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.fontDao().loadAll()
                .map(FontConverter::toModel) + internalFonts()
        }
    }

    override suspend fun loadFonts(query: String): List<FontModel> {
        return withContext(dispatcherProvider.io()) {
            val defaultFonts = internalFonts()
                .filter { it.fontName.contains(query, ignoreCase = true) }
            val userFonts = appDatabase.fontDao().loadAll(query)
                .map(FontConverter::toModel)
            userFonts + defaultFonts
        }
    }

    override suspend fun importFont(fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val fontUuid = UUID.randomUUID().toString()
                val fontName = fileUri.path.orEmpty().substringAfterLast('/')
                val fontFile = File(context.cacheDir, fontUuid)
                if (!fontFile.exists()) {
                    fontFile.createNewFile()
                    inputStream.copyTo(fontFile.outputStream())
                }
                val fontModel = FontModel(
                    fontUuid = fontUuid,
                    fontName = fontName,
                    fontPath = fontFile.absolutePath,
                    isExternal = true,
                )
                appDatabase.fontDao().insert(FontConverter.toEntity(fontModel))
            }
        }
    }

    override suspend fun selectFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.fontType = fontModel.fontPath
        }
    }

    override suspend fun removeFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            val fontFile = File(context.cacheDir, fontModel.fontUuid)
            if (fontFile.exists()) {
                fontFile.deleteRecursively()
            }
            appDatabase.fontDao().delete(FontConverter.toEntity(fontModel))
            if (settingsManager.fontType == fontModel.fontPath) {
                settingsManager.remove(SettingsManager.KEY_FONT_TYPE)
            }
        }
    }

    private fun internalFonts(): List<FontModel> {
        return listOf(
            FontModel(
                fontUuid = "droid_sans_mono",
                fontName = "Droid Sans Mono",
                fontPath = "file:///android_asset/fonts/droid_sans_mono.ttf",
                isExternal = false,
            ),
            FontModel(
                fontUuid = "jetbrains_mono",
                fontName = "JetBrains Mono",
                fontPath = "file:///android_asset/fonts/jetbrains_mono.ttf",
                isExternal = false,
            ),
            FontModel(
                fontUuid = "fira_code",
                fontName = "Fira Code",
                fontPath = "file:///android_asset/fonts/fira_code.ttf",
                isExternal = false,
            ),
            FontModel(
                fontUuid = "source_code_pro",
                fontName = "Source Code Pro",
                fontPath = "file:///android_asset/fonts/source_code_pro.ttf",
                isExternal = false,
            ),
            FontModel(
                fontUuid = "anonymous_pro",
                fontName = "Anonymous Pro",
                fontPath = "file:///android_asset/fonts/anonymous_pro.ttf",
                isExternal = false,
            ),
            FontModel(
                fontUuid = "dejavu_sans_mono",
                fontName = "DejaVu Sans Mono",
                fontPath = "file:///android_asset/fonts/dejavu_sans_mono.ttf",
                isExternal = false,
            ),
        )
    }
}