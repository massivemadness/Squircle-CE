package com.blacksquircle.ui.feature.settings.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.data.utils.getRawFileText
import com.blacksquircle.ui.feature.settings.databinding.FragmentChangelogBinding
import com.blacksquircle.ui.feature.settings.ui.adapters.ReleaseAdapter
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChangeLogFragment : Fragment(R.layout.fragment_changelog) {

    private val viewModel by activityViewModels<SettingsViewModel>()
    private val binding by viewBinding(FragmentChangelogBinding::bind)

    private lateinit var adapter: ReleaseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ReleaseAdapter().also {
            adapter = it
        }

        val changelog = requireContext().getRawFileText(R.raw.changelog)
        viewModel.fetchChangeLog(changelog)
    }

    private fun observeViewModel() {
        viewModel.changelogState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { adapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}