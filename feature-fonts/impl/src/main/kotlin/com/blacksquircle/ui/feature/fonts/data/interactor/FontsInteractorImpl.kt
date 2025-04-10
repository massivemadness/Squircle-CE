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
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.fonts.data.model.AssetsFont
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

internal class FontsInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val context: Context,
) : FontsInteractor {

    private val fontsDir: File
        get() = Directories.fontsDir(context)

    override suspend fun loadFont(fontId: String): Typeface {
        return withContext(dispatcherProvider.io()) {
            try {
                /** Check if [fontId] is in assets */
                val assetsFont = AssetsFont.find(fontId)
                if (assetsFont != null) {
                    val fontPath = assetsFont.fontUri.substring(ASSET_PATH.length)
                    return@withContext Typeface.createFromAsset(context.assets, fontPath)
                }

                /** Couldn't find in assets, look in [fontsDir] */
                val fontFile = File(fontsDir, fontId + FileType.TTF)
                if (fontFile.exists()) {
                    val fontPath = fontFile.absolutePath
                    return@withContext Typeface.createFromFile(fontPath)
                }

                throw IllegalStateException("Font $fontId not found")
            } catch (e: Exception) {
                Timber.e(e, "Couldn't load font: ${e.message}")
                Typeface.MONOSPACE
            }
        }
    }

    companion object {
        private const val ASSET_PATH = "file:///android_asset/"
    }
}