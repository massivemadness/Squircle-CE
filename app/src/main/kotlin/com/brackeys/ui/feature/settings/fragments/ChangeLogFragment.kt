/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.brackeys.ui.R
import com.brackeys.ui.databinding.FragmentChangelogBinding
import com.brackeys.ui.feature.base.fragments.BaseFragment
import com.brackeys.ui.feature.settings.adapters.ReleaseAdapter
import com.brackeys.ui.feature.settings.viewmodel.SettingsViewModel
import com.brackeys.ui.utils.extensions.getRawFileText

class ChangeLogFragment : BaseFragment(R.layout.fragment_changelog) {

    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var binding: FragmentChangelogBinding
    private lateinit var adapter: ReleaseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangelogBinding.bind(view)
        observeViewModel()

        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ReleaseAdapter()
            .also { adapter = it }

        val changeLog = requireContext().getRawFileText(R.raw.changelog)
        viewModel.fetchChangeLog(changeLog)
    }

    private fun observeViewModel() {
        viewModel.changelogEvent.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}