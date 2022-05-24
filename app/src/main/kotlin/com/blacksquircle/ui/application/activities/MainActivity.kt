/*
 * Copyright 2022 Squircle IDE contributors.
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
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.blacksquircle.ui.R
import com.blacksquircle.ui.application.dialogs.ConfirmExitDialog
import com.blacksquircle.ui.application.viewmodel.MainViewModel
import com.blacksquircle.ui.core.ui.extensions.fragment
import com.blacksquircle.ui.core.ui.extensions.fullscreenMode
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.core.ui.navigation.BackPressedHandler
import com.blacksquircle.ui.core.ui.navigation.DrawerHandler
import com.blacksquircle.ui.databinding.ActivityMainBinding
import com.blacksquircle.ui.feature.editor.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.editor.ui.fragments.EditorFragment
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.feature.explorer.ui.fragments.ExplorerFragment
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.utils.extensions.multiplyDraggingEdgeSizeBy
import com.blacksquircle.ui.utils.extensions.resolveFilePath
import com.blacksquircle.ui.utils.inappupdate.InAppUpdate
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerHandler {

    @Inject
    lateinit var inAppUpdate: InAppUpdate

    private val mainViewModel by viewModels<MainViewModel>()
    private val explorerViewModel by viewModels<ExplorerViewModel>()
    private val editorViewModel by viewModels<EditorViewModel>()

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
            editorViewModel.openFileEvent.value = DocumentConverter.toModel(it)
        }
        editorViewModel.openPropertiesEvent.observe(this) {
            explorerViewModel.openPropertiesEvent.value = it
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val contentUri = intent.data ?: return
            Log.d(TAG, "Handle external content uri = $contentUri")

            val filePath = resolveFilePath(contentUri)
            Log.d(TAG, "Does it looks like a valid file path? ($filePath)")

            val isValidFile = try {
                File(filePath).exists()
            } catch (e: Exception) {
                false
            }
            Log.d(TAG, "isValidFile = $isValidFile")

            if (isValidFile) {
                val file = File(filePath)
                mainViewModel.handleDocument(file) {
                    editorViewModel.loadFiles()
                }
            } else {
                Log.d(TAG, "Invalid path")
                showToast(R.string.message_file_not_found)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}