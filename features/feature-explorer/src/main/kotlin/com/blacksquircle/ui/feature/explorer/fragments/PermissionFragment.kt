/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.explorer.fragments

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.afollestad.materialdialogs.MaterialDialog
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.FragmentPermissionBinding
import com.blacksquircle.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionFragment : Fragment(R.layout.fragment_permission) {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val binding: FragmentPermissionBinding by viewBinding()
    private val navController: NavController by navController()

    private val requestResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            onSuccess()
        } else {
            onFailure()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.checkStorageAccess(::onSuccess, ::onFailure)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionAccess.setOnClickListener {
            activity?.checkStorageAccess(
                onSuccess = ::onSuccess,
                onFailure = {
                    activity?.requestStorageAccess(
                        showRequestDialog = { requestResult.launch(WRITE_EXTERNAL_STORAGE) },
                        showExplanationDialog = { showExplanationDialog(it) }
                    )
                }
            )
        }
    }

    private fun onSuccess() {
        viewModel.showAppBarEvent.value = true
        val destination = PermissionFragmentDirections.toDirectoryFragment(null)
        navController.navigate(destination)
    }

    private fun onFailure() {
        viewModel.showAppBarEvent.value = false
    }

    private fun showExplanationDialog(intent: Intent) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_storage_access)
            message(R.string.dialog_message_storage_access)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_continue) {
                startActivity(intent)
            }
        }
    }
}