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
import com.blacksquircle.ui.redux.internal.NextBuilder

abstract class Reducer<S : MVIState, I : MVIIntent, E : MVIEffect> {

    private val builder = NextBuilder<S, I, E>()

    protected val state: S
        get() = builder.state

    internal fun reduce(state: S, intent: I): Next<S, I, E> {
        builder.bind(state)
        reduce(intent)
        return builder.build()
    }

    abstract fun reduce(intent: I)

    protected fun state(block: S.() -> S) = builder.state(block)
    // protected fun intent(intent: I) = builder.intent(intent)
    protected fun effect(effect: E) = builder.effect(effect)
}