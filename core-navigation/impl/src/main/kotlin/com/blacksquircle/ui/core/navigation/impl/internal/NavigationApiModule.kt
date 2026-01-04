package com.blacksquircle.ui.core.navigation.impl.internal

import com.blacksquircle.ui.core.common.coroutines.DispatcherProvider
import com.blacksquircle.ui.core.navigation.api.deeplink.DeeplinkManager
import com.blacksquircle.ui.core.navigation.api.deeplink.handler.DeeplinkHandler
import com.blacksquircle.ui.core.navigation.impl.deeplink.DeeplinkManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NavigationApiModule {

    @Provides
    @Singleton
    fun provideDeeplinkManager(
        deeplinkHandlers: Set<@JvmSuppressWildcards DeeplinkHandler>,
        dispatcherProvider: DispatcherProvider,
    ): DeeplinkManager {
        return DeeplinkManagerImpl(
            deeplinkHandlers = deeplinkHandlers,
            dispatcherProvider = dispatcherProvider,
        )
    }
}