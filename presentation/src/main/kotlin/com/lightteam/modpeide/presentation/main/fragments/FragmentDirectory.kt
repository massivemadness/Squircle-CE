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

package com.lightteam.modpeide.presentation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.presentation.main.adapters.FileAdapter
import com.lightteam.modpeide.presentation.main.adapters.interfaces.SelectionTransfer

class FragmentDirectory : DaggerFragment(), SelectionTransfer {

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var adapter: FileAdapter

    private lateinit var binding: FragmentDirectoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_directory, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        setupListeners()
        setupObservers()
    }

    override fun onClick(fileModel: FileModel) {
        if(fileModel.isFolder) {
            viewModel.tabsEvent.value = fileModel
        } else {
            viewModel.documentEvent.value = fileModel
        }
    }

    override fun onLongClick(fileModel: FileModel): Boolean {
        if(fileModel.isFolder) {
            viewModel.tabsEvent.value = fileModel
        } else {
            //show options menu
        }
        return true
    }

    private fun setupListeners() { }

    private fun setupObservers() { //too complicated
        viewModel.listEvent.observe(this.viewLifecycleOwner, Observer { list ->
            adapter.setData(list)
        })
    }
}