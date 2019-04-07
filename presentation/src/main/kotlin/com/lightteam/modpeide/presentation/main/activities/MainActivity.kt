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
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.main.activities.interfaces.OnPanelClickListener
import com.lightteam.modpeide.presentation.main.activities.utils.ToolbarManager
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import com.lightteam.modpeide.presentation.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.extensions.launchActivity
import javax.inject.Inject

class MainActivity : BaseActivity(), OnPanelClickListener {

    companion object {
        const val REQUEST_READ_WRITE = 1 //Запрос на разрешения через диалог
        const val REQUEST_READ_WRITE2 = 2 //Запрос на разрешения через активность настроек
    }

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var toolbarManager: ToolbarManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        toolbarManager.init(binding)
        onConfigurationChanged(resources.configuration)
        checkPermissions()
        setupListeners()
        setupObservers()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbarManager.landscape()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbarManager.portrait()
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

    private fun setupListeners() { }

    private fun setupObservers() {
        viewModel.toastEvent.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.documentEvent.observe(this, Observer {
            //open the file
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        })
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
