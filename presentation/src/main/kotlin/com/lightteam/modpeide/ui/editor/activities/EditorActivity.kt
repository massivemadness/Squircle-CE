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

package com.lightteam.modpeide.ui.editor.activities

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivityMainBinding
import com.lightteam.modpeide.ui.base.activities.BaseActivity
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import javax.inject.Inject

class EditorActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: EditorViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        observeViewModel()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            showExitDialog()
        }
    }

    fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(this, Observer {
            showToast(it)
        })
    }

    private fun showExitDialog() {
        MaterialDialog(this@EditorActivity).show {
            title(R.string.dialog_title_exit)
            message(R.string.dialog_message_exit)
            negativeButton(R.string.action_no)
            positiveButton(R.string.action_yes, click = {
                finish()
            })
        }
    }
}
