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

import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIIntent
import com.blacksquircle.ui.redux.MVIState
import com.blacksquircle.ui.redux.internal.Next

class CompoundReducer<S : MVIState, I : MVIIntent, E : MVIEffect>(
    private val reducers: List<Reducer<S, I, E>>
) : Reducer<S, I, E>() {

    override fun reduce(intent: I) {
        val next = reducers.fold(Next<S, I, E>()) { acc, reducer ->
            val reduce = reducer.reduce(state, intent)
            reduce.state?.let { state { it } }
            reduce.merge(acc)
        }
        next.state?.let { state { it } }
        next.effects.forEach { effect(it) }
    }
}
