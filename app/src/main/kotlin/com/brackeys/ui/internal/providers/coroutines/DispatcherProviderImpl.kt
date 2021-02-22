package com.brackeys.ui.internal.providers.coroutines

import com.brackeys.ui.domain.providers.coroutines.DispatcherProvider
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override fun io() = Dispatchers.IO
    override fun computation() = Dispatchers.Default
    override fun mainThread() = Dispatchers.Main
}