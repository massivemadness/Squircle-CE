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

package com.blacksquircle.ui.data.repository.fonts

import com.blacksquircle.ui.data.converter.FontConverter
import com.blacksquircle.ui.data.storage.database.AppDatabase
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.domain.providers.coroutine.DispatcherProvider
import com.blacksquircle.ui.domain.repository.fonts.FontsRepository
import kotlinx.coroutines.withContext

class FontsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase
) : FontsRepository {

    override suspend fun fetchFonts(searchQuery: String): List<FontModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.fontDao()
                .loadAll(searchQuery)
                .map(FontConverter::toModel)
        }
    }

    override suspend fun createFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            appDatabase.fontDao().insert(FontConverter.toEntity(fontModel))
        }
    }

    override suspend fun removeFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            appDatabase.fontDao().delete(FontConverter.toEntity(fontModel))
            if (settingsManager.fontType == fontModel.fontPath) {
                settingsManager.remove(SettingsManager.KEY_FONT_TYPE)
            }
        }
    }

    override suspend fun selectFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.fontType = fontModel.fontPath
        }
    }
}