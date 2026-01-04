/*
 * Copyright Squircle CE contributors.
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
inline fun <reified T> ResultEffect(
    resultKey: String = T::class.toString(),
    crossinline onResult: suspend (T) -> Unit
) {
    LaunchedEffect(resultKey, ResultEventBus.channelMap[resultKey]) {
        ResultEventBus.getResultFlow<T>(resultKey)?.collect { result ->
            onResult(result as T)
        }
    }
}

object ResultEventBus {

    val channelMap = mutableMapOf<String, Channel<Any?>>()

    inline fun <reified T> getResultFlow(resultKey: String = T::class.toString()): Flow<Any?>? {
        return channelMap[resultKey]?.receiveAsFlow()
    }

    inline fun <reified T> sendResult(resultKey: String = T::class.toString(), result: T) {
        if (!channelMap.contains(resultKey)) {
            channelMap[resultKey] = Channel(Channel.BUFFERED)
        }
        channelMap[resultKey]?.trySend(result)
    }
}