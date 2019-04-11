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

import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivitySettingsBinding
import com.lightteam.modpeide.presentation.base.activities.BaseActivity
import com.lightteam.modpeide.presentation.main.activities.MainActivity
import com.lightteam.modpeide.presentation.settings.viewmodel.SettingsViewModel
import com.lightteam.modpeide.utils.extensions.launchActivity
import javax.inject.Inject

class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        setupListeners()
        setupObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
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
    }

    private fun setupObservers() {
        lifecycle.addObserver(viewModel)
        viewModel.fullscreenEvent.observe(this, Observer { isFullscreen ->
            if(isFullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        })
        viewModel.themeEvent.observe(this, Observer {
            MaterialDialog(this).show {
                title(R.string.dialog_title_apply_changes)
                message(R.string.dialog_message_apply_changes)
                negativeButton(R.string.action_cancel)
                positiveButton(R.string.action_restart, click = {
                    finishAffinity()
                    launchActivity<MainActivity>()
                })
            }
        })
    }
}