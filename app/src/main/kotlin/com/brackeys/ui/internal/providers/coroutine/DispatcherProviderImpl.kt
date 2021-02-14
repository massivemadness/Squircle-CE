package com.brackeys.ui.internal.providers.coroutine

import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override fun io() = Dispatchers.IO
    override fun computation() = Dispatchers.Default
    override fun mainThread() = Dispatchers.Main
}