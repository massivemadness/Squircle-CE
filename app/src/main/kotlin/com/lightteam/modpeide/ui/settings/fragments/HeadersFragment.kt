/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentHeadersBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.settings.adapters.PreferenceAdapter
import com.lightteam.modpeide.ui.settings.adapters.item.PreferenceItem
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeadersFragment : BaseFragment(R.layout.fragment_headers), OnItemClickListener<PreferenceItem> {

    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHeadersBinding
    private lateinit var adapter: PreferenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchHeaders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadersBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = PreferenceAdapter(this)
            .also { adapter = it }
    }

    override fun onClick(item: PreferenceItem) {
        navController.navigate(item.navigationId)
    }

    private fun observeViewModel() {
        viewModel.headersEvent.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}