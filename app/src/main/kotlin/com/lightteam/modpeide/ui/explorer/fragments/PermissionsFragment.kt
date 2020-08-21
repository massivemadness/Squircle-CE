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
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentPermissionsBinding
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.utils.extensions.hasExternalStorageAccess
import com.lightteam.modpeide.utils.extensions.launchPermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionsFragment : BaseFragment(R.layout.fragment_permissions) {

    companion object {
        private const val REQUEST_CODE_STORAGE_DIALOG = 1
        private const val REQUEST_CODE_STORAGE_ACTIVITY = 2
    }

    private val viewModel: ExplorerViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentPermissionsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPermissionsBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.actionAccess.setOnClickListener {
            requestPermissionsUsingDialog()
        }

        checkIfPermissionsAlreadyGiven(false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_DIALOG -> {
                checkIfPermissionsAlreadyGiven(true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STORAGE_ACTIVITY -> {
                checkIfPermissionsAlreadyGiven(true)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.hasAccessEvent.observe(viewLifecycleOwner, { hasAccess ->
            viewModel.hasPermission.set(hasAccess)
            if (hasAccess) {
                val destination = PermissionsFragmentDirections.toDirectoryFragment(null)
                navController.navigate(destination)
            } else {
                binding.actionAccess.setOnClickListener {
                    requestPermissionsUsingActivity()
                }
            }
        })
    }

    private fun checkIfPermissionsAlreadyGiven(shouldUseActivity: Boolean) {
        if (requireContext().hasExternalStorageAccess()) {
            viewModel.hasAccessEvent.value = true
        } else if (shouldUseActivity) {
            viewModel.hasAccessEvent.value = false
        }
    }

    private fun requestPermissionsUsingDialog() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_STORAGE_DIALOG
        )
    }

    private fun requestPermissionsUsingActivity() {
        launchPermissionActivity(REQUEST_CODE_STORAGE_ACTIVITY)
    }
}