/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.adapters.PreferenceAdapter
import com.blacksquircle.ui.feature.settings.adapters.item.PreferenceItem
import com.blacksquircle.ui.feature.settings.databinding.FragmentHeadersBinding
import com.blacksquircle.ui.feature.settings.viewmodel.SettingsViewModel
import com.blacksquircle.ui.utils.adapters.OnItemClickListener
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeadersFragment : Fragment(R.layout.fragment_headers) {

    private val viewModel: SettingsViewModel by activityViewModels()
    private val binding: FragmentHeadersBinding by viewBinding()
    private val navController: NavController by navController()

    private lateinit var adapter: PreferenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchHeaders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = PreferenceAdapter(object : OnItemClickListener<PreferenceItem> {
            override fun onClick(item: PreferenceItem) {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()
                navController.navigate(item.navigationId, null, navOptions)
            }
        }).also {
            adapter = it
        }
    }

    private fun observeViewModel() {
        viewModel.headersEvent.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}