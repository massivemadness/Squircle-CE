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

package com.lightteam.modpeide.presentation.settings.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lightteam.modpeide.BaseApplication
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivitySettingsBinding
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.settings.viewmodel.SettingsViewModel
import javax.inject.Inject

class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observePreferences()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewModel = viewModel

        setupListeners()
        setupObservers()
    }

    override fun onBackPressed() {
        viewModel.backEvent.value = true
    }

    private fun setupListeners() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.buttonUnlockFeatures.setOnClickListener {
            val dialog = AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setView(R.layout.dialog_store)
                .show()

            dialog.findViewById<View>(R.id.button_get_it)?.setOnClickListener {
                val packageName = BaseApplication.ULTIMATE
                try {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    startActivity(intent)
                }
                dialog.dismiss()
            }
            dialog.findViewById<View>(R.id.button_continue)?.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun setupObservers() {
        viewModel.fullscreenEvent.observe(this, Observer { isFullscreen ->
            if(isFullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        })
    }
}