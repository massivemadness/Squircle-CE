package com.brackeys.ui.internal.di.explorer

import com.brackeys.ui.data.repository.explorer.ExplorerRepositoryImpl
import com.brackeys.ui.data.storage.keyvalue.SettingsManager
import com.brackeys.ui.domain.providers.coroutines.DispatcherProvider
import com.brackeys.ui.domain.repository.explorer.ExplorerRepository
import com.brackeys.ui.filesystem.base.Filesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ExplorerModule {

    @Provides
    @ViewModelScoped
    fun provideExplorerRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        @Named("Local")
        filesystem: Filesystem
    ): ExplorerRepository {
        return ExplorerRepositoryImpl(dispatcherProvider, settingsManager, filesystem)
    }
}