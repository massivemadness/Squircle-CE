/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.core.effect

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun NavResultEffect(resultKey: String, onEvent: (Bundle) -> Unit) {
    LaunchedEffect(resultKey) {
        val channelFlow = NavigationBus.subscribe(resultKey)
        channelFlow.collect { bundle ->
            onEvent(bundle)
        }
    }
    DisposableEffect(resultKey) {
        onDispose {
            NavigationBus.unsubscribe(resultKey)
        }
    }
}

fun sendNavigationResult(key: String, result: Bundle) {
    NavigationBus.emit(key, result)
}

private object NavigationBus {

    private val eventMap = mutableMapOf<String, Channel<Bundle>>()

    fun emit(key: String, event: Bundle) {
        eventMap[key]?.trySend(event)
    }

    fun subscribe(key: String): Flow<Bundle> {
        return eventMap.getOrPut(key) {
            Channel(Channel.BUFFERED)
        }.receiveAsFlow()
    }

    fun unsubscribe(key: String) {
        eventMap.remove(key)
    }
}