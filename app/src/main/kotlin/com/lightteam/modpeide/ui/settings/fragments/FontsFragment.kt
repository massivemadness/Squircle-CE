package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentFontsBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.dialogs.DialogStore
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.settings.adapter.FontAdapter
import com.lightteam.modpeide.ui.settings.adapter.item.FontItem
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel
import com.lightteam.modpeide.utils.extensions.isUltimate
import javax.inject.Inject

class FontsFragment : BaseFragment(), OnItemClickListener<FontItem> {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private lateinit var binding: FragmentFontsBinding
    private lateinit var adapter: FontAdapter

    override fun layoutId(): Int = R.layout.fragment_fonts

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFontsBinding.bind(view)
        observeViewModel()

        adapter = FontAdapter(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        viewModel.fetchFonts()
    }

    override fun onClick(item: FontItem) {
        if (item.isPaid && !requireContext().isUltimate()) {
            DialogStore.Builder(requireContext()).show()
        } else {
            viewModel.selectFont(item)
        }
    }

    private fun observeViewModel() {
        viewModel.fontsEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.selectionEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = String.format(getString(R.string.message_selected), it))
        })
    }
}