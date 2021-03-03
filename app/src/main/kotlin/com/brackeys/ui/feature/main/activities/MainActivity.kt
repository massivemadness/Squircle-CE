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

package com.brackeys.ui.feature.main.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.brackeys.ui.R
import com.brackeys.ui.databinding.ActivityMainBinding
import com.brackeys.ui.feature.editor.fragments.EditorFragment
import com.brackeys.ui.feature.explorer.fragments.ExplorerFragment
import com.brackeys.ui.feature.main.dialogs.ConfirmExitDialog
import com.brackeys.ui.feature.main.utils.OnBackPressedHandler
import com.brackeys.ui.feature.main.viewmodel.MainViewModel
import com.brackeys.ui.utils.extensions.fragment
import com.brackeys.ui.utils.extensions.multiplyDraggingEdgeSizeBy
import com.brackeys.ui.utils.inappupdate.InAppUpdate
import com.brackeys.ui.utils.inappupdate.InAppUpdateImpl
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val inAppUpdate: InAppUpdate by lazy { InAppUpdateImpl(applicationContext) }

    private lateinit var binding: ActivityMainBinding
    private lateinit var editorOnBackPressedHandler: OnBackPressedHandler
    private lateinit var explorerOnBackPressedHandler: OnBackPressedHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(R.color.colorBackground)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        editorOnBackPressedHandler = supportFragmentManager
            .fragment<EditorFragment>(R.id.fragment_editor)
        explorerOnBackPressedHandler = supportFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

        binding.drawerLayout?.multiplyDraggingEdgeSizeBy(2)

        inAppUpdate.checkForUpdates(this) {
            Snackbar.make(binding.root, R.string.message_in_app_update_ready, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart) { inAppUpdate.completeUpdate() }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.fullScreenMode) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            if (!explorerOnBackPressedHandler.handleOnBackPressed()) {
                viewModel.closeDrawerEvent.call()
            }
        } else {
            if (!editorOnBackPressedHandler.handleOnBackPressed()) {
                if (viewModel.confirmExit) {
                    ConfirmExitDialog().show(supportFragmentManager, ConfirmExitDialog.DIALOG_TAG)
                } else {
                    finish()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.openDrawerEvent.observe(this) {
            binding.drawerLayout?.openDrawer(GravityCompat.START)
        }
        viewModel.closeDrawerEvent.observe(this) {
            binding.drawerLayout?.closeDrawer(GravityCompat.START)
        }
    }
}