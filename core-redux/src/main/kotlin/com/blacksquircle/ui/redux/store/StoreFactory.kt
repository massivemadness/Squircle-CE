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
import com.blacksquircle.ui.redux.middleware.Middleware
import com.blacksquircle.ui.redux.reducer.Reducer

fun <S : MVIState, I : MVIIntent, E : MVIEffect> produceStore(
    initialState: S,
    initialIntent: I? = null,
    reducer: Reducer<S, I, E>,
    middlewares: List<Middleware<S, I, E>>,
): Store<S, I, E> {
    return StoreImpl(
        initialState = initialState,
        initialIntent = initialIntent,
        reducer = reducer,
        middlewares = middlewares
    )
}