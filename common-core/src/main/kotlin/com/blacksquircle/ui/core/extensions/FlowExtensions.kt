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

package com.blacksquircle.ui.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/** https://github.com/Kotlin/kotlinx.coroutines/issues/2631 */
fun <T> StateFlow<T>.onEach(
    scope: CoroutineScope,
    action: suspend (T) -> Unit
): StateFlow<T> = onEach(action).stateIn(
    scope = scope,
    started = SharingStarted.Eagerly,
    initialValue = value
)

/** https://github.com/Kotlin/kotlinx.coroutines/issues/2631 */
fun <T, R> StateFlow<T>.map(
    scope: CoroutineScope,
    transform: (T) -> R
): StateFlow<R> = map(transform).stateIn(
    scope = scope,
    started = SharingStarted.Eagerly,
    initialValue = transform(value)
)