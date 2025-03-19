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

package com.blacksquircle.ui.feature.fonts.data.interactor

import android.content.Context
import android.graphics.Typeface
import com.blacksquircle.ui.core.contract.FileType
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.Directories
import com.blacksquircle.ui.core.storage.database.dao.font.FontDao
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.fonts.data.model.InternalFont
import com.blacksquircle.ui.feature.fonts.data.utils.createTypefaceFromPath
import kotlinx.coroutines.withContext
import java.io.File

internal class FontsInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val fontDao: FontDao,
    private val context: Context,
) : FontsInteractor {

    private val fontsDir: File
        get() = Directories.fontsDir(context)

    override suspend fun current(): Typeface {
        return withContext(dispatcherProvider.io()) {
            val fontUuid = settingsManager.fontType

            val internalFont = InternalFont.find(fontUuid)
            if (internalFont != null) {
                return@withContext context.createTypefaceFromPath(internalFont.fontUri)
            }

            val externalFont = fontDao.load(fontUuid)
            if (externalFont != null) {
                val fontFile = File(fontsDir, externalFont.fontUuid + FileType.TTF)
                return@withContext context.createTypefaceFromPath(fontFile.absolutePath)
            }

            throw IllegalStateException("Font with id $fontUuid not found")
        }
    }
}