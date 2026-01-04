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

import com.blacksquircle.ui.redux.MVIAction
import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIState
import com.blacksquircle.ui.redux.middleware.Middleware
import com.blacksquircle.ui.redux.reducer.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class StoreImpl<S : MVIState, A : MVIAction, E : MVIEffect>(
    initialState: S,
    private val initialAction: A? = null,
    private val reducer: Reducer<S, A, E>,
    private val middlewares: List<Middleware<S, A>>,
) : Store<S, A, E> {

    private val actions = MutableSharedFlow<A>(extraBufferCapacity = 64)

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state

    private val _effects = Channel<E>(Channel.UNLIMITED)
    override val effects: Flow<E> = _effects.receiveAsFlow()

    override fun wire(scope: CoroutineScope) {
        scope.launch {
            actions.collect { action ->
                val update = reducer.reduce(_state.value, action)
                update.state?.let { _state.value = it }
                update.effects.forEach { _effects.send(it) }
                update.actions.forEach { actions.emit(it) }
            }
        }

        middlewares.forEach { middleware ->
            scope.launch {
                middleware.bind(state, actions).collect { action ->
                    actions.emit(action)
                }
            }
        }

        scope.launch {
            initialAction?.let { action ->
                actions.emit(action)
            }
        }
    }

    override fun dispatch(action: A) {
        actions.tryEmit(action)
    }
}