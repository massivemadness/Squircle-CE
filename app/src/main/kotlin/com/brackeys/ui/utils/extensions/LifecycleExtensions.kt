package com.brackeys.ui.utils.extensions

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchEvent(
    liveDataProgressBar: MutableLiveData<Boolean>? = null,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    liveDataProgressBar?.value = true
    return launch(context, start, block).also {
        it.invokeOnCompletion {
            liveDataProgressBar?.value = false
        }
    }
}