/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.extensions.observeFragmentResult
import com.blacksquircle.ui.core.extensions.viewModels
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.themes.internal.ThemesComponent
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import javax.inject.Inject
import javax.inject.Provider

internal class ThemesFragment : Fragment() {

    @Inject
    lateinit var viewModelProvider: Provider<ThemesViewModel>

    private val viewModel by viewModels<ThemesViewModel> { viewModelProvider.get() }

    override fun onAttach(context: Context) {
        ThemesComponent.buildOrGet(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        SquircleTheme {
            ThemesScreen(
                navController = findNavController(),
                viewModel = viewModel, // TODO ???
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFragmentResult(KEY_SAVE) {
            viewModel.loadThemes()
        }
    }

    companion object {
        const val KEY_SAVE = "KEY_SAVE"
    }
}