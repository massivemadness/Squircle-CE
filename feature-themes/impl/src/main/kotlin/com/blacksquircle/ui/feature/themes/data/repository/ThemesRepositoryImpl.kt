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

package com.blacksquircle.ui.feature.themes.data.repository

import android.content.Context
import android.net.Uri
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.database.AppDatabase
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.ExternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.InternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.withContext
import java.io.BufferedReader

internal class ThemesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val context: Context,
) : ThemesRepository {

    private val themeSerializer = GsonBuilder()
        .setPrettyPrinting()
        .create()

    override suspend fun current(): ThemeModel {
        return withContext(dispatcherProvider.io()) {
            val colorScheme = settingsManager.colorScheme
            InternalTheme.find(colorScheme) ?: loadTheme(colorScheme)
        }
    }

    override suspend fun loadThemes(query: String): List<ThemeModel> {
        return withContext(dispatcherProvider.io()) {
            val defaultThemes = InternalTheme.entries
                .map(InternalTheme::theme)
                .filter { it.name.contains(query, ignoreCase = true) }
            val userThemes = appDatabase.themeDao().loadAll(query)
                .map(ThemeMapper::toModel)
                .filter { it.name.contains(query, ignoreCase = true) }
            userThemes + defaultThemes
        }
    }

    override suspend fun loadTheme(uuid: String): ThemeModel {
        return withContext(dispatcherProvider.io()) {
            val themeEntity = appDatabase.themeDao().load(uuid)
            ThemeMapper.toModel(themeEntity)
        }
    }

    override suspend fun importTheme(fileUri: Uri): ThemeModel {
        return withContext(dispatcherProvider.io()) {
            context.contentResolver.openInputStream(fileUri)?.use {
                val fileJson = it.bufferedReader().use(BufferedReader::readText)
                val externalTheme = themeSerializer.fromJson(fileJson, ExternalTheme::class.java)
                return@withContext ThemeMapper.toModel(externalTheme)
            }
            throw IllegalStateException("Unable to open input stream")
        }
    }

    override suspend fun exportTheme(themeModel: ThemeModel, fileUri: Uri) {
        withContext(dispatcherProvider.io()) {
            val externalTheme = ThemeMapper.toExternalTheme(themeModel)
            val fileJson = themeSerializer.toJson(externalTheme)
            context.contentResolver.openOutputStream(fileUri)?.use { output ->
                output.write(fileJson.toByteArray())
                output.flush()
            }
        }
    }

    override suspend fun createTheme(themeModel: ThemeModel) {
        return withContext(dispatcherProvider.io()) {
            val themeEntity = ThemeMapper.toEntity(themeModel)
            appDatabase.themeDao().insert(themeEntity)
        }
    }

    override suspend fun removeTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            appDatabase.themeDao().delete(ThemeMapper.toEntity(themeModel))
            if (settingsManager.colorScheme == themeModel.uuid) {
                settingsManager.remove(SettingsManager.KEY_COLOR_SCHEME)
            }
        }
    }

    override suspend fun selectTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.colorScheme = themeModel.uuid
        }
    }
}