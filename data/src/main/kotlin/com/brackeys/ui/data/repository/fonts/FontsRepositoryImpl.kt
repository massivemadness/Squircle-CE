package com.brackeys.ui.data.repository.fonts

import com.brackeys.ui.data.converter.FontConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.domain.model.font.FontModel
import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import com.brackeys.ui.domain.repository.fonts.FontsRepository
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
            if (settingsManager.getFontType().get() == fontModel.fontPath) {
                settingsManager.getFontType().delete()
            }
        }
    }

    override suspend fun selectFont(fontModel: FontModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.getFontType().set(fontModel.fontPath)
        }
    }
}