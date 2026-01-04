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

data class Update<S : MVIState, A : MVIAction, E : MVIEffect>(
    val state: S? = null,
    val actions: List<A> = emptyList(),
    val effects: List<E> = emptyList()
) {

    fun merge(other: Update<S, A, E>): Update<S, A, E> = Update(
        state = other.state ?: state,
        actions = actions + other.actions,
        effects = effects + other.effects
    )
}