/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.application

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.blacksquircle.ui.application.extensions.toComposeColors
import com.blacksquircle.ui.application.splash.SplashScreen
import com.blacksquircle.ui.application.update.KEY_INSTALL_UPDATE
import com.blacksquircle.ui.application.update.rememberInAppUpdate
import com.blacksquircle.ui.core.effect.NavResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.fullscreenMode
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.animation.NavigationTransition
import com.blacksquircle.ui.ds.extensions.LocalNavController
import com.blacksquircle.ui.feature.editor.api.navigation.EditorScreen
import com.blacksquircle.ui.feature.editor.ui.editorGraph
import com.blacksquircle.ui.feature.explorer.ui.explorerGraph
import com.blacksquircle.ui.feature.fonts.ui.fontsGraph
import com.blacksquircle.ui.feature.git.ui.gitGraph
import com.blacksquircle.ui.feature.servers.ui.serversGraph
import com.blacksquircle.ui.feature.settings.ui.settingsGraph
import com.blacksquircle.ui.feature.shortcuts.ui.shortcutsGraph
import com.blacksquircle.ui.feature.themes.ui.themesGraph
import com.blacksquircle.ui.internal.di.AppComponent

@Composable
internal fun MainScreen(
    savedInstanceState: Bundle? = null,
    viewModel: MainViewModel = daggerViewModel { context ->
        val component = AppComponent.buildOrGet(context)
        MainViewModel.Factory().also(component::inject)
    },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val colors = toComposeColors(viewState.colorScheme)
    if (viewState.isLoading) {
        SquircleTheme(darkTheme = true) {
            SplashScreen()
        }
        return
    }

    val navController = rememberNavController()
    val inAppUpdate = rememberInAppUpdate()

    SquircleTheme(colors = colors) {
        Surface(color = SquircleTheme.colors.colorBackgroundPrimary) {
            CompositionLocalProvider(LocalNavController provides navController) {
                NavHost(
                    navController = navController,
                    startDestination = EditorScreen,
                    enterTransition = { NavigationTransition.EnterTransition },
                    exitTransition = { NavigationTransition.ExitTransition },
                    popEnterTransition = { NavigationTransition.PopEnterTransition },
                    popExitTransition = { NavigationTransition.PopExitTransition },
                ) {
                    mainGraph(navController)
                    editorGraph(navController)
                    explorerGraph(navController)
                    fontsGraph(navController)
                    gitGraph(navController)
                    serversGraph(navController)
                    settingsGraph(navController)
                    shortcutsGraph(navController)
                    themesGraph(navController)
                }
            }
        }
    }

    val activity = LocalActivity.current as? ComponentActivity
    DisposableEffect(Unit) {
        val consumer = Consumer<Intent> { intent ->
            viewModel.onNewIntent(intent)
        }
        activity?.addOnNewIntentListener(consumer)
        onDispose {
            activity?.removeOnNewIntentListener(consumer)
        }
    }

    LaunchedEffect(Unit) {
        if (savedInstanceState == null) {
            activity?.intent?.let { intent ->
                viewModel.onNewIntent(intent)
            }
            inAppUpdate.checkForUpdates {
                viewModel.onUpdateAvailable()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> activity?.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    NavResultEffect(KEY_INSTALL_UPDATE) {
        inAppUpdate.installUpdate()
    }

    LaunchedEffect(viewState.fullscreenMode) {
        activity?.window?.fullscreenMode(viewState.fullscreenMode)
    }
}