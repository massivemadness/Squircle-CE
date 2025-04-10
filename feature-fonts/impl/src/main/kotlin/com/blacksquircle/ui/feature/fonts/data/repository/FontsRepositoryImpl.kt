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
import com.blacksquircle.ui.core.contract.FileType
import com.blacksquircle.ui.core.database.dao.font.FontDao
import com.blacksquircle.ui.core.database.entity.font.FontEntity
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.data.mapper.FontMapper
import com.blacksquircle.ui.feature.fonts.data.model.AssetsFont
import com.blacksquircle.ui.feature.fonts.data.utils.createTypefaceFromPath
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

internal class FontsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val fontDao: FontDao,
    private val context: Context,
) : FontsRepository {

    private val fontsDir: File
        get() = Directories.fontsDir(context)

    override suspend fun loadFonts(query: String): List<FontModel> {
        return withContext(dispatcherProvider.io()) {
            val defaultFonts = AssetsFont.entries
                .filter { it.name.contains(query, ignoreCase = true) }
                .map { font ->
                    val typeface = context.createTypefaceFromPath(font.fontUri)
                    FontMapper.toModel(font, typeface)
                }
            val userFonts = fontDao.loadAll()
                .filter { it.fontName.contains(query, ignoreCase = true) }
                .map { font ->
                    val fontFile = File(fontsDir, font.fontUuid + FileType.TTF)
                    val typeface = context.createTypefaceFromPath(fontFile.absolutePath)
                    FontMapper.toModel(font, typeface)
                }

            userFonts + defaultFonts
        }
    }

    override suspend fun importFont(fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val fontUuid = UUID.randomUUID().toString()
                val fontName = Uri.decode(fileUri.toString()).substringAfterLast(File.separator)
                val fontFile = File(fontsDir, fontUuid + FileType.TTF)
                if (!fontFile.exists()) {
                    fontFile.createNewFile()
                    inputStream.copyTo(fontFile.outputStream())
                }
                val fontEntity = FontEntity(
                    fontUuid = fontUuid,
                    fontName = fontName,
                )
                fontDao.insert(fontEntity)
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
            val fontFile = File(fontsDir, fontModel.uuid + FileType.TTF)
            if (fontFile.exists()) {
                fontFile.deleteRecursively()
            }
            fontDao.delete(fontModel.uuid)
            if (settingsManager.fontType == fontModel.uuid) {
                settingsManager.remove(SettingsManager.KEY_FONT_TYPE)
            }
        }
    }
}