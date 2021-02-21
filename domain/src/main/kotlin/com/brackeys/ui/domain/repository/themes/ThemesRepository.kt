package com.brackeys.ui.domain.repository.themes

import android.net.Uri
import com.brackeys.ui.domain.model.themes.Meta
import com.brackeys.ui.domain.model.themes.PropertyItem
import com.brackeys.ui.domain.model.themes.ThemeModel

interface ThemesRepository {
    suspend fun fetchThemes(searchQuery: String): List<ThemeModel>
    suspend fun fetchTheme(uuid: String): ThemeModel

    suspend fun importTheme(uri: Uri): ThemeModel
    suspend fun exportTheme(themeModel: ThemeModel)

    suspend fun createTheme(meta: Meta, properties: List<PropertyItem>)
    suspend fun removeTheme(themeModel: ThemeModel)
    suspend fun selectTheme(themeModel: ThemeModel)
}