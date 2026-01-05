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

package com.blacksquircle.ui.feature.fonts.ui.fonts.store

import com.blacksquircle.ui.feature.fonts.ui.fonts.store.middleware.FontsMiddleware
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.middleware.FontsSearchMiddleware
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.reducer.FontsReducer
import com.blacksquircle.ui.redux.store.Store
import com.blacksquircle.ui.redux.store.produceStore
import javax.inject.Inject

internal class FontsStore @Inject constructor(
    private val fontsReducer: FontsReducer,
    private val fontsMiddleware: FontsMiddleware,
    private val fontsSearchMiddleware: FontsSearchMiddleware,
) : Store<FontsState, FontsAction, FontsEvent> by produceStore(
        initialState = FontsState(),
        initialAction = FontsAction.Init,
        reducer = fontsReducer,
        middlewares = listOf(
            fontsMiddleware,
            fontsSearchMiddleware,
        )
    )