package com.brackeys.ui.domain.providers.coroutine

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    fun io(): CoroutineDispatcher
    fun computation(): CoroutineDispatcher
    fun mainThread(): CoroutineDispatcher
}