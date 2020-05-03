package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.databinding.FragmentThemesBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.dialogs.DialogStore
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.settings.adapter.ThemeAdapter
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel
import com.lightteam.modpeide.utils.extensions.isUltimate
import javax.inject.Inject

class ThemesFragment : BaseFragment(), OnItemClickListener<Theme> {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private lateinit var binding: FragmentThemesBinding
    private lateinit var adapter: ThemeAdapter

    override fun layoutId(): Int = R.layout.fragment_themes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentThemesBinding.bind(view)
        observeViewModel()

        adapter = ThemeAdapter(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        viewModel.fetchThemes()
    }

    override fun onClick(item: Theme) {
        if (item.isPaid && !requireContext().isUltimate()) {
            DialogStore.Builder(requireContext()).show()
        } else {
            viewModel.selectTheme(item)
        }
    }

    private fun observeViewModel() {
        viewModel.themesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.selectionEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = String.format(getString(R.string.message_selected), it))
        })
    }
}