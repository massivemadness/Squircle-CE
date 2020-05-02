package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentHeadersBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.settings.adapter.PreferenceAdapter
import com.lightteam.modpeide.ui.settings.adapter.item.PreferenceItem
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel
import javax.inject.Inject

class HeadersFragment : BaseFragment(), OnItemClickListener<PreferenceItem> {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHeadersBinding
    private lateinit var adapter: PreferenceAdapter

    override fun layoutId(): Int = R.layout.fragment_headers

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHeadersBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        adapter = PreferenceAdapter(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        viewModel.fetchHeaders()
    }

    override fun onClick(item: PreferenceItem) {
        navController.navigate(item.navigationId)
    }

    private fun observeViewModel() {
        viewModel.headersEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}