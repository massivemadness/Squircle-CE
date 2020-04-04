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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentPermissionsBinding
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.utils.extensions.launchPermissionActivity
import javax.inject.Inject

class PermissionsFragment : BaseFragment() {

    companion object {
        const val REQUEST_STORAGE_DIALOG = 1
        const val REQUEST_STORAGE_ACTIVITY = 2
    }

    @Inject
    lateinit var viewModel: ExplorerViewModel

    private lateinit var binding: FragmentPermissionsBinding
    private lateinit var navController: NavController

    override fun layoutId(): Int = R.layout.fragment_permissions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        navController = findNavController()
        observeViewModel()

        binding.actionAccess.setOnClickListener {
            requestPermissionsUsingDialog()
        }

        checkIfPermissionsAlreadyGiven()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_DIALOG -> {
                viewModel.hasAccessEvent.value = grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPermissionsAlreadyGiven()
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        viewModel.hasAccessEvent.observe(viewLifecycleOwner, Observer { hasAccess ->
            viewModel.hasPermission.set(hasAccess)
            if (hasAccess) {
                val action = PermissionsFragmentDirections.toDirectoryFragment(null)
                navController.navigate(action)
            } else {
                binding.actionAccess.setOnClickListener {
                    requestPermissionsUsingActivity()
                }
            }
        })
    }

    private fun checkIfPermissionsAlreadyGiven() {
        if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasAccessEvent.value = true
        }
    }

    private fun requestPermissionsUsingDialog() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_DIALOG
        )
    }

    private fun requestPermissionsUsingActivity() {
        launchPermissionActivity(REQUEST_STORAGE_ACTIVITY)
    }
}