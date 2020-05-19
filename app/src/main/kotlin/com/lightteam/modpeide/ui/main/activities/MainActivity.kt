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

package com.lightteam.modpeide.ui.main.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.ActivityResult
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.ui.base.activities.BaseActivity
import com.lightteam.modpeide.ui.base.utils.OnBackPressedHandler
import com.lightteam.modpeide.ui.explorer.fragments.ExplorerFragment
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.utils.extensions.fragment
import com.lightteam.modpeide.utils.extensions.getColour
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_UPDATE = 10
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var backPressedHandler: OnBackPressedHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        backPressedHandler = supportFragmentManager
            .fragment<ExplorerFragment>(R.id.fragment_explorer)

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
            if (!backPressedHandler.handleOnBackPressed()) {
                viewModel.closeDrawerEvent.call()
            }
        } else {
            if (viewModel.backEvent.value != false) {
                MaterialDialog(this).show {
                    title(R.string.dialog_title_exit)
                    message(R.string.dialog_message_exit)
                    negativeButton(R.string.action_no)
                    positiveButton(R.string.action_yes) {
                        finish()
                    }
                }
            } else {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.updateEvent.observe(this, Observer {
            val appUpdateManager = it.first
            val appUpdateInfo = it.second
            val appUpdateType = it.third
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                appUpdateType,
                this,
                REQUEST_CODE_UPDATE
            )
        })
        viewModel.installEvent.observe(this, Observer {
            Snackbar.make(binding.root, R.string.message_in_app_update_ready, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getColour(R.color.colorPrimary))
                .setAction(R.string.action_restart) { viewModel.completeUpdate() }
                .show()
        })
        viewModel.fullscreenEvent.observe(this, Observer { enabled ->
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        })
        viewModel.openDrawerEvent.observe(this, Observer {
            binding.drawerLayout?.openDrawer(GravityCompat.START)
        })
        viewModel.closeDrawerEvent.observe(this, Observer {
            binding.drawerLayout?.closeDrawer(GravityCompat.START)
        })

        viewModel.observePreferences()
    }
}
