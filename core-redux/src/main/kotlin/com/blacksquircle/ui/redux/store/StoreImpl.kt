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

package com.blacksquircle.ui.redux.store

import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIIntent
import com.blacksquircle.ui.redux.MVIState
import com.blacksquircle.ui.redux.internal.Next
import com.blacksquircle.ui.redux.middleware.Middleware
import com.blacksquircle.ui.redux.reducer.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class StoreImpl<S : MVIState, I : MVIIntent, E : MVIEffect>(
    initialState: S,
    initialIntent: I? = null,
    private val reducer: Reducer<S, I, E>,
    private val middlewares: List<Middleware<S, I, E>>,
): Store<S, I, E> {

    private val intents = Channel<I>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state

    private val _effects = Channel<E>(Channel.UNLIMITED)
    override val effects: Flow<E> = _effects.receiveAsFlow()

    init {
        initialIntent?.let { intent ->
            intents.trySend(intent)
        }
    }

    override fun wire(scope: CoroutineScope) {
        scope.launch {
            intents.consumeAsFlow().collect { intent ->
                val next = reducer.bind(_state.value, intent)
                update(next)

                middlewares.forEach { middleware ->
                    launch {
                        val next = middleware.bind(_state.value, intent)
                        update(next)
                    }
                }
            }
        }
    }

    override fun dispatch(intent: I) {
        intents.trySend(intent)
    }

    private suspend fun update(next: Next<S, I, E>) {
        next.state?.let { _state.value = it }
        next.effects.forEach { _effects.send(it) }
        next.intents.forEach { intents.send(it) }
    }
}