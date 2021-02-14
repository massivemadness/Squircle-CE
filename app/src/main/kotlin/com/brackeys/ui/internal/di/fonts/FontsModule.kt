package com.brackeys.ui.internal.di.fonts

import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.repository.fonts.FontsRepositoryImpl
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import com.brackeys.ui.domain.repository.fonts.FontsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object FontsModule {

    @Provides
    @ViewModelScoped
    fun provideFontsRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        appDatabase: AppDatabase
    ): FontsRepository {
        return FontsRepositoryImpl(dispatcherProvider, settingsManager, appDatabase)
    }
}