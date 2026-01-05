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

package com.blacksquircle.ui.feature.fonts.ui.reducer

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.fonts.createFontModel
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsAction
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.FontsState
import com.blacksquircle.ui.feature.fonts.ui.fonts.store.reducer.FontsReducer
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FontsReducerTest {

    private val stringProvider = mockk<StringProvider>(relaxed = true)

    private val reducer = FontsReducer(stringProvider)

    @Test
    fun `When select clicked Then update selected uuid`() = runTest {
        // Given
        val fontModel = createFontModel(uuid = "1")
        val state = FontsState(
            fonts = listOf(fontModel),
            selectedUuid = "0"
        )

        // When
        val action = FontsAction.UiAction.OnSelectClicked(fontModel)
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(selectedUuid = fontModel.uuid)
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When remove clicked Then remove font`() = runTest {
        // Given
        val fontModel = createFontModel(uuid = "2")
        val state = FontsState(
            fonts = listOf(fontModel),
            selectedUuid = fontModel.uuid
        )

        // When
        val action = FontsAction.CommandAction.FontRemoved(fontModel, "1")
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(
            fonts = emptyList(),
            selectedUuid = "1"
        )
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When user typing in search bar Then update query`() = runTest {
        // Given
        val state = FontsState(searchQuery = "")

        // When
        val action = FontsAction.UiAction.OnQueryChanged("qwerty")
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(searchQuery = "qwerty")
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When user clearing search query Then reload font list`() = runTest {
        // Given
        val state = FontsState(searchQuery = "qwerty")

        // When
        val action = FontsAction.UiAction.OnClearQueryClicked
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(searchQuery = "")
        assertEquals(reducedState, update.state)
    }
}