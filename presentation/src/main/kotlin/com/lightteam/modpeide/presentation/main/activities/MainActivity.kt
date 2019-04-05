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
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import com.lightteam.modpeide.presentation.settings.activities.SettingsActivity
import com.lightteam.modpeide.utils.extensions.launchActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val REQUEST_READ_WRITE = 1
        const val REQUEST_READ_WRITE2 = 2
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkPermissions()
        setupToolbar()
        setupListeners()
        setupObservers()
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

    @SuppressLint("RtlHardcoded")
    private fun setupToolbar() {
        binding.actionMenuDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.actionMenuSave.setOnClickListener {  }
        binding.actionMenuFile.setOnClickListener {  }
        binding.actionMenuEdit.setOnClickListener {  }
        binding.actionMenuSearch.setOnClickListener {  }
        binding.actionMenuTools.setOnClickListener {  }
        binding.actionMenuUndo.setOnClickListener {  }
        binding.actionMenuRedo.setOnClickListener {  }
        binding.actionMenuOverflow.setOnClickListener {
            launchActivity<SettingsActivity>()
        }
    }

    private fun setupListeners() { }

    private fun setupObservers() {
        viewModel.documentEvent.observe(this, Observer {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        })
    }
}
