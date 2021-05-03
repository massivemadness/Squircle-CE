/*
 * Copyright 2021 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.explorer.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.brackeys.ui.feature.explorer.R
import com.brackeys.ui.feature.explorer.contracts.StoragePermission
import com.brackeys.ui.feature.explorer.databinding.FragmentPermissionsBinding
import com.brackeys.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.brackeys.ui.utils.extensions.hasExternalStorageAccess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionsFragment : Fragment(R.layout.fragment_permissions) {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val requestPermission: ActivityResultLauncher<Boolean> =
        registerForActivityResult(StoragePermission()) {
            if (requireContext().hasExternalStorageAccess()) {
                onSuccess()
            } else {
                onFailure()
            }
        }

    private lateinit var binding: FragmentPermissionsBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPermissionsBinding.bind(view)
        navController = findNavController()

        binding.actionAccess.setOnClickListener {
            requestPermissionsUsingDialog()
        }

        val storagePermissions = requireContext().hasExternalStorageAccess()
        viewModel.showAppBarEvent.value = storagePermissions
        if (storagePermissions) {
            onSuccess()
        }
    }

    private fun onSuccess() {
        viewModel.showAppBarEvent.value = true
        val destination = PermissionsFragmentDirections.toDirectoryFragment(null)
        navController.navigate(destination)
    }

    private fun onFailure() {
        viewModel.showAppBarEvent.value = false
        binding.actionAccess.setOnClickListener {
            requestPermissionsUsingActivity()
        }
    }

    private fun requestPermissionsUsingDialog() = requestPermission.launch(false)
    private fun requestPermissionsUsingActivity() = requestPermission.launch(true)
}