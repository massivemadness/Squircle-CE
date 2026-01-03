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
import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIIntent
import com.blacksquircle.ui.redux.MVIState
import com.blacksquircle.ui.redux.mapper.ViewState
import com.blacksquircle.ui.redux.store.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class StoreViewModel<S : MVIState, I : MVIIntent, E : MVIEffect> : ViewModel() {

    abstract val store: Store<S, I, E>
    abstract val viewState: StateFlow<ViewState>

    val state: StateFlow<S> = store.state
    val effects: Flow<E> = store.effects

    init {
        store.wire(viewModelScope)
    }

    fun dispatch(intent: I) {
        store.dispatch(intent)
    }
}