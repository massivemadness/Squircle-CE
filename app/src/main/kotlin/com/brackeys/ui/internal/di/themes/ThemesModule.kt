package com.brackeys.ui.internal.di.themes

import android.content.Context
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.repository.themes.ThemesRepositoryImpl
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import com.brackeys.ui.domain.repository.themes.ThemesRepository
import com.brackeys.ui.filesystem.base.Filesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ThemesModule {

    @Provides
    @ViewModelScoped
    fun provideThemesRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        appDatabase: AppDatabase,
        @Named("Local")
        filesystem: Filesystem,
        @ApplicationContext context: Context
    ): ThemesRepository {
        return ThemesRepositoryImpl(
            dispatcherProvider,
            settingsManager,
            appDatabase,
            filesystem,
            context
        )
    }
}