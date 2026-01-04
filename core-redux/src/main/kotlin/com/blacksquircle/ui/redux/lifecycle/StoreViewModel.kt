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

package com.blacksquircle.ui.redux.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.redux.MVIAction
import com.blacksquircle.ui.redux.MVIEvent
import com.blacksquircle.ui.redux.MVIState
import com.blacksquircle.ui.redux.store.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class StoreViewModel<S : MVIState, A : MVIAction, E : MVIEvent>(
    private val store: Store<S, A, E>,
) : ViewModel() {

    val state: StateFlow<S> = store.state
    val events: Flow<E> = store.events

    init {
        store.wire(viewModelScope)
    }

    fun dispatch(action: A) {
        store.dispatch(action)
    }
}