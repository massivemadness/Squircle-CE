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

package com.blacksquircle.ui.redux.internal

import com.blacksquircle.ui.redux.MVIEffect
import com.blacksquircle.ui.redux.MVIIntent
import com.blacksquircle.ui.redux.MVIState

class NextBuilder<S : MVIState, I : MVIIntent, E : MVIEffect> {

    private lateinit var state: S
    private val intents = mutableListOf<I>()
    private val effects = mutableListOf<E>()

    internal fun bind(state: S) {
        this.state = state
        intents.clear()
        effects.clear()
    }

    fun state(block: S.() -> S) {
        state = state.block()
    }

    fun intent(intent: I) {
        intents += intent
    }

    fun effect(effect: E) {
        effects += effect
    }

    internal fun build(): Next<S, I, E> = Next(
        state = state,
        intents = intents.toList(),
        effects = effects.toList()
    )
}
