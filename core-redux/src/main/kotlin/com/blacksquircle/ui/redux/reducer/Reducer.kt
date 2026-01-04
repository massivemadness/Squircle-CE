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

abstract class Reducer<S : MVIState, A : MVIAction, E : MVIEffect> {

    protected lateinit var state: S
        private set

    private val actions = mutableListOf<A>()
    private val effects = mutableListOf<E>()

    abstract fun reduce(action: A)

    internal fun reduce(state: S, action: A): Update<S, A, E> {
        this.state = state
        actions.clear()
        effects.clear()

        reduce(action)

        return Update(
            state = this.state,
            actions = this.actions,
            effects = this.effects,
        )
    }

    protected fun state(block: S.() -> S) {
        state = state.block()
    }

    protected fun action(action: A) {
        actions += action
    }

    protected fun effect(effect: E) {
        effects += effect
    }

    internal fun build(): Update<S, A, E> = Update(
        state = state,
        actions = actions.toList(),
        effects = effects.toList()
    )
}