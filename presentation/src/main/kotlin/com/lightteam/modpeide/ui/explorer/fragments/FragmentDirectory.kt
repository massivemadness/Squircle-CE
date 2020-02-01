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

package com.lightteam.modpeide.ui.explorer.fragments

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter
import com.lightteam.modpeide.ui.explorer.adapters.interfaces.ItemCallback
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import javax.inject.Inject

class FragmentDirectory : BaseFragment(), ItemCallback {

    @Inject
    lateinit var viewModel: ExplorerViewModel
    @Inject
    lateinit var adapter: FileAdapter

    private val args: FragmentDirectoryArgs by navArgs()
    private var fileList: List<FileModel> = emptyList()

    private lateinit var binding: FragmentDirectoryBinding
    private lateinit var navController: NavController

    override fun layoutId(): Int = R.layout.fragment_directory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.newTab(args.fileModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        navController = findNavController()
        observeViewModel()
        loadDirectory()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun onClick(fileModel: FileModel) {
        val action = FragmentDirectoryDirections.toDirectoryFragment(fileModel)
        navController.navigate(action)
    }

    override fun onLongClick(fileModel: FileModel): Boolean {
        return true
    }

    private fun observeViewModel() {
        viewModel.filesEvent.observe(viewLifecycleOwner, Observer {
            fileList = it
            viewModel.searchList = fileList
            adapter.submitList(it)
        })
        viewModel.filesUpdateEvent.observe(viewLifecycleOwner, Observer {
            loadDirectory()
        })
        viewModel.searchEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun loadDirectory() {
        viewModel.searchList = fileList //фильтрация по текущему списку
        val fileModel = args.fileModel
        if (fileModel == null) {
            viewModel.provideDefaultDirectory()
        } else {
            viewModel.provideDirectory(fileModel)
        }
    }
}