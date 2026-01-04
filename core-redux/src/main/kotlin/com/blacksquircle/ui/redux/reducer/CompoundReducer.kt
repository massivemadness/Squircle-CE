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

package com.blacksquircle.ui.redux.reducer

import com.blacksquircle.ui.redux.MVIAction
import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIState

class CompoundReducer<S : MVIState, A : MVIAction, E : MVIEffect>(
    private val reducers: List<Reducer<S, A, E>>
) : Reducer<S, A, E>() {

    override fun reduce(action: A) {
        val update = reducers.fold(Update<S, A, E>()) { acc, reducer ->
            val update = reducer.reduce(state, action)
            update.state?.let { state { it } }
            update.merge(acc)
        }
        update.state?.let { state { it } }
        update.effects.forEach { effect(it) }
    }
}