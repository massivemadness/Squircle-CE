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

package com.lightteam.modpeide.ui.settings.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.ActivitySettingsBinding
import com.lightteam.modpeide.ui.base.activities.BaseActivity
import com.lightteam.modpeide.ui.settings.viewmodel.SettingsViewModel
import com.lightteam.modpeide.utils.extensions.fragment
import com.lightteam.modpeide.utils.extensions.isUltimate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        navController = supportFragmentManager
            .fragment<NavHostFragment>(R.id.nav_host).navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = destination.label
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonContainer.isGone = isUltimate()
        binding.buttonUnlockFeatures.setOnClickListener {
            navController.navigate(R.id.storeDialog)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun observeViewModel() {
        viewModel.fullscreenEvent.observe(this) { enabled ->
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }

        viewModel.observeSettings()
    }
}