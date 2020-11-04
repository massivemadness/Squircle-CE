/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.main.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.brackeys.ui.R
import com.brackeys.ui.databinding.ActivityMainBinding
import com.brackeys.ui.feature.base.activities.BaseActivity
import com.brackeys.ui.feature.base.utils.OnBackPressedHandler
import com.brackeys.ui.feature.editor.fragments.EditorFragment
import com.brackeys.ui.feature.explorer.fragments.ExplorerFragment
import com.brackeys.ui.feature.main.dialogs.ConfirmExitDialog
import com.brackeys.ui.feature.main.viewmodel.MainViewModel
import com.brackeys.ui.utils.extensions.fragment
import com.brackeys.ui.utils.extensions.multiplyDraggingEdgeSizeBy
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.ActivityResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_UPDATE = 10
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var editorOnBackPressedHandler: OnBackPressedHandler
    private lateinit var explorerOnBackPressedHandler: OnBackPressedHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        editorOnBackPressedHandler = supportFragmentManager
            .fragment<EditorFragment>(R.id.fragment_editor)
        explorerOnBackPressedHandler = supportFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

        binding.drawerLayout?.multiplyDraggingEdgeSizeBy(2)

        viewModel.checkForUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> { /* approved */ }
                Activity.RESULT_CANCELED -> { /* rejected */ }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    showToast(R.string.message_in_app_update_failed)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            if (!explorerOnBackPressedHandler.handleOnBackPressed()) {
                viewModel.closeDrawerEvent.call()
            }
        } else {
            if (!editorOnBackPressedHandler.handleOnBackPressed()) {
                if (viewModel.confirmExitEvent.value != false) {
                    ConfirmExitDialog().show(supportFragmentManager, ConfirmExitDialog.DIALOG_TAG)
                } else {
                    finish()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.updateEvent.observe(this) {
            val appUpdateManager = it.first
            val appUpdateInfo = it.second
            val appUpdateType = it.third
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                appUpdateType,
                this,
                REQUEST_CODE_UPDATE
            )
        }
        viewModel.installEvent.observe(this) {
            Snackbar.make(binding.root, R.string.message_in_app_update_ready, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart) { viewModel.completeUpdate() }
                .show()
        }
        viewModel.fullscreenEvent.observe(this) { enabled ->
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
        viewModel.openDrawerEvent.observe(this) {
            binding.drawerLayout?.openDrawer(GravityCompat.START)
        }
        viewModel.closeDrawerEvent.observe(this) {
            binding.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        viewModel.observeSettings()
    }
}