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

package com.blacksquircle.ui.feature.themes.ui.reducer

import android.graphics.Typeface
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.feature.themes.createThemeModel
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesAction
import com.blacksquircle.ui.feature.themes.ui.themes.store.ThemesState
import com.blacksquircle.ui.feature.themes.ui.themes.store.reducer.ThemesReducer
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ThemesReducerTest {

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val typeface = mockk<Typeface>()

    private val reducer = ThemesReducer(stringProvider)

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns typeface
    }

    @Test
    fun `When select clicked Then update selected uuid`() = runTest {
        // Given
        val themeModel = createThemeModel(uuid = "1")
        val state = ThemesState(
            themes = listOf(themeModel),
            selectedUuid = "0"
        )

        // When
        val action = ThemesAction.UiAction.OnSelectClicked(themeModel)
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(selectedUuid = themeModel.uuid)
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When remove clicked Then remove theme`() = runTest {
        // Given
        val themeModel = createThemeModel(uuid = "2")
        val state = ThemesState(
            themes = listOf(themeModel),
            selectedUuid = themeModel.uuid
        )

        // When
        val action = ThemesAction.CommandAction.ThemeRemoved(themeModel, "1")
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(
            themes = emptyList(),
            selectedUuid = "1"
        )
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When user typing in search bar Then update query`() = runTest {
        // Given
        val state = ThemesState(searchQuery = "")

        // When
        val action = ThemesAction.UiAction.OnQueryChanged("qwerty")
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(searchQuery = "qwerty")
        assertEquals(reducedState, update.state)
    }

    @Test
    fun `When user clearing search query Then reload theme list`() = runTest {
        // Given
        val state = ThemesState(searchQuery = "qwerty")

        // When
        val action = ThemesAction.UiAction.OnClearQueryClicked
        val update = reducer.reduce(state, action)

        // Then
        val reducedState = state.copy(searchQuery = "")
        assertEquals(reducedState, update.state)
    }
}