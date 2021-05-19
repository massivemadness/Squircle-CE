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

package com.blacksquircle.ui.application.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.blacksquircle.ui.R
import com.blacksquircle.ui.application.dialogs.ConfirmExitDialog
import com.blacksquircle.ui.application.viewmodel.MainViewModel
import com.blacksquircle.ui.databinding.ActivityMainBinding
import com.blacksquircle.ui.feature.editor.fragments.EditorFragment
import com.blacksquircle.ui.feature.editor.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.explorer.fragments.ExplorerFragment
import com.blacksquircle.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.utils.extensions.fragment
import com.blacksquircle.ui.utils.extensions.fullscreenMode
import com.blacksquircle.ui.utils.extensions.multiplyDraggingEdgeSizeBy
import com.blacksquircle.ui.utils.extensions.showToast
import com.blacksquircle.ui.utils.inappupdate.InAppUpdate
import com.blacksquircle.ui.utils.interfaces.BackPressedHandler
import com.blacksquircle.ui.utils.interfaces.DrawerHandler
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerHandler {

    @Inject
    lateinit var inAppUpdate: InAppUpdate

    private val mainViewModel: MainViewModel by viewModels()
    private val explorerViewModel: ExplorerViewModel by viewModels()
    private val editorViewModel: EditorViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var editorBackPressedHandler: BackPressedHandler
    private lateinit var explorerBackPressedHandler: BackPressedHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(R.color.colorBackground)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        editorBackPressedHandler = supportFragmentManager
            .fragment<EditorFragment>(R.id.fragment_editor)
        explorerBackPressedHandler = supportFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

        binding.drawerLayout?.multiplyDraggingEdgeSizeBy(2)

        inAppUpdate.checkForUpdates(this) {
            Snackbar.make(binding.root, R.string.message_in_app_update_ready, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart) { inAppUpdate.completeUpdate() }
                .show()
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        window.fullscreenMode(mainViewModel.fullScreenMode)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            if (!explorerBackPressedHandler.handleOnBackPressed()) {
                closeDrawer()
            }
        } else {
            if (!editorBackPressedHandler.handleOnBackPressed()) {
                if (mainViewModel.confirmExit) {
                    ConfirmExitDialog().show(supportFragmentManager, ConfirmExitDialog.DIALOG_TAG)
                } else {
                    finish()
                }
            }
        }
    }

    override fun openDrawer() {
        binding.drawerLayout?.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        binding.drawerLayout?.closeDrawer(GravityCompat.START)
    }

    private fun observeViewModel() {
        mainViewModel.toastEvent.observe(this) {
            showToast(it)
        }
        explorerViewModel.openFileEvent.observe(this) {
            editorViewModel.openFileEvent.value = it
        }
        editorViewModel.openPropertiesEvent.observe(this) {
            explorerViewModel.openPropertiesEvent.value = it
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            mainViewModel.handleIntent(intent) {
                editorViewModel.loadFiles()
            }
        }
    }
}