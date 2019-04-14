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

package com.lightteam.modpeide.presentation.main.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.tabs.TabLayout
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.main.activities.interfaces.OnPanelClickListener
import com.lightteam.modpeide.presentation.main.activities.utils.ToolbarManager
import com.lightteam.modpeide.presentation.main.adapters.DocumentAdapter
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import com.lightteam.modpeide.presentation.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.extensions.launchActivity
import javax.inject.Inject

class MainActivity : BaseActivity(),
    OnPanelClickListener,
    TabLayout.OnTabSelectedListener {

    companion object {
        const val REQUEST_READ_WRITE = 1 //Запрос на разрешения через диалог
        const val REQUEST_READ_WRITE2 = 2 //Запрос на разрешения через активность настроек
    }

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager
    @Inject
    lateinit var adapter: DocumentAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observePreferences()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        toolbarManager.bind(binding)
        onConfigurationChanged(resources.configuration)
        setupListeners()
        setupObservers()
        checkPermissions()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toolbarManager.setOrientation(newConfig.orientation)
    }

    override fun onBackPressed() {
        if(!closeDrawersIfNecessary()) {
            if(viewModel.backEvent.value!!) {
                MaterialDialog(this).show {
                    title(R.string.dialog_title_exit)
                    message(R.string.dialog_message_exit)
                    negativeButton(R.string.action_no)
                    positiveButton(R.string.action_yes, click = {
                        finish()
                    })
                }
            } else {
                finish()
            }
        }
    }

    // region PERMISSIONS

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_WRITE -> {
                viewModel.hasAccessEvent.value = grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkPermissions()
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasAccessEvent.value = true
        }
    }

    // endregion PERMISSIONS

    // region TABS

    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewModel.loadFile(adapter.get(tab.position))
    }

    // endregion TABS

    private fun setupListeners() {
        binding.tabDocumentLayout.addOnTabSelectedListener(this)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerOpened(drawerView: View) {
                closeKeyboard()
            }
        })
    }

    private fun setupObservers() {
        viewModel.toastEvent.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.documentTabEvent.observe(this, Observer {
            closeDrawersIfNecessary()
            addTab(it)
        })
        viewModel.documentTextEvent.observe(this, Observer {
            binding.editor.setText(it)
        })

        // region PREFERENCES

        viewModel.fullscreenEvent.observe(this, Observer { isFullscreen ->
            if(isFullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        })

        viewModel.fontSizeEvent.observe(this, Observer { fontSize ->
            val configuration = binding.editor.getConfiguration().copy(fontSize = fontSize)
            binding.editor.setConfiguration(configuration)
        })
        viewModel.fontTypeEvent.observe(this, Observer {  fontType ->
            val configuration = binding.editor.getConfiguration().copy(fontType = fontType)
            binding.editor.setConfiguration(configuration)
        })

        // endregion PREFERENCES

        viewModel.loadAllFiles()
    }

    private fun addTab(documentModel: DocumentModel) {
        val tab = binding.tabDocumentLayout.newTab()
        tab.text = documentModel.name
        tab.setCustomView(R.layout.item_tab_document)
        val view = tab.customView?.findViewById<View>(R.id.item_icon)
        view?.setOnClickListener {
            adapter.removeAt(tab.position)
            binding.tabDocumentLayout.removeTab(tab)

            viewModel.removeDocument(documentModel)
            viewModel.noDocumentsIndicator.set(adapter.isEmpty())
        }
        adapter.add(documentModel)
        binding.tabDocumentLayout.addTab(tab)
        binding.tabDocumentLayout.post { tab.select() }

        viewModel.noDocumentsIndicator.set(adapter.isEmpty())
    }

    private fun closeKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val windowToken = currentFocus?.windowToken
        val hideType = InputMethodManager.HIDE_NOT_ALWAYS
        inputManager.hideSoftInputFromWindow(windowToken, hideType)
    }

    @SuppressLint("RtlHardcoded")
    private fun closeDrawersIfNecessary(): Boolean {
        val isOpen = binding.drawerLayout.isDrawerOpen(Gravity.LEFT)
        if(isOpen) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        }
        return isOpen
    }

    // region PANEL

    @SuppressLint("RtlHardcoded")
    override fun onDrawerButton() {
        binding.drawerLayout.openDrawer(Gravity.LEFT)
    }

    override fun onNewButton() {
    }

    override fun onOpenButton() {
    }

    override fun onSaveButton() {
    }

    override fun onPropertiesButton() {
    }

    override fun onCloseButton() {
    }

    override fun onCutButton() {
    }

    override fun onCopyButton() {
    }

    override fun onPasteButton() {
    }

    override fun onSelectAllButton() {
    }

    override fun onSelectLineButton() {
    }

    override fun onDeleteLineButton() {
    }

    override fun onDuplicateLineButton() {
    }

    override fun onFindButton() {
    }

    override fun onReplaceAllButton() {
    }

    override fun onGoToLineButton() {
    }

    override fun onSyntaxValidatorButton() {
    }

    override fun onInsertColorButton() {
    }

    override fun onUndoButton() {
    }

    override fun onRedoButton() {
    }

    override fun onSettingsButton() {
        launchActivity<SettingsActivity>()
    }

    // endregion PANEL
}
