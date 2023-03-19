/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.application.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.updatePadding
import com.blacksquircle.ui.R
import com.blacksquircle.ui.application.viewmodel.MainViewModel
import com.blacksquircle.ui.core.extensions.applySystemWindowInsets
import com.blacksquircle.ui.core.extensions.decorFitsSystemWindows
import com.blacksquircle.ui.core.extensions.fullscreenMode
import com.blacksquircle.ui.databinding.ActivityMainBinding
import com.blacksquircle.ui.utils.InAppUpdate
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var inAppUpdate: InAppUpdate

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorFitsSystemWindows(false)
        window.fullscreenMode(mainViewModel.fullScreenMode)

        binding.navHost.applySystemWindowInsets(false) { left, _, right, _ ->
            binding.navHost.updatePadding(left = left, right = right)
        }

        inAppUpdate.checkForUpdates(this) {
            Snackbar.make(binding.root, R.string.message_in_app_update_ready, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart) { inAppUpdate.completeUpdate() }
                .show()
        }

        if (savedInstanceState == null) {
            mainViewModel.handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mainViewModel.handleIntent(intent)
    }
}