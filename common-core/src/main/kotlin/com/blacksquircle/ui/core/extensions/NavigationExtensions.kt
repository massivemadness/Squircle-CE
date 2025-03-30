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

package com.blacksquircle.ui.core.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.blacksquircle.ui.core.navigation.Screen

data class NavAction(val id: Int, val args: Bundle? = null)

fun NavController.navigateTo(
    screen: Screen,
    options: NavOptions? = null,
    extras: Navigator.Extras? = null,
) {
    when (screen.route) {
        is String -> navigate(
            deepLink = screen.route.toUri(),
            navOptions = options,
            navigatorExtras = extras
        )

        is NavDirections -> navigate(
            resId = screen.route.actionId,
            args = screen.route.arguments,
            navOptions = options,
            navigatorExtras = extras
        )

        is NavAction -> navigate(
            resId = screen.route.id,
            args = screen.route.args,
            navOptions = options,
            navigatorExtras = extras
        )

        is Int -> navigate(
            resId = screen.route,
            args = null,
            navOptions = options,
            navigatorExtras = extras
        )

        else -> throw IllegalArgumentException("Route is not supported")
    }
}

fun Fragment.sendFragmentResult(resultKey: String, vararg pairs: Pair<String, Any?>) {
    requireActivity().supportFragmentManager.setFragmentResult(
        resultKey,
        bundleOf(*pairs)
    )
}

fun Fragment.sendFragmentResult(resultKey: String, bundle: Bundle = Bundle.EMPTY) {
    requireActivity().supportFragmentManager.setFragmentResult(resultKey, bundle)
}

fun Fragment.observeFragmentResult(resultKey: String, onResult: (Bundle) -> Unit) {
    val fragmentResultListener = FragmentResultListener { _, result ->
        onResult(result)
    }
    requireActivity().supportFragmentManager.setFragmentResultListener(
        resultKey,
        this,
        fragmentResultListener
    )
}

fun NavController.sendResult(key: String, result: Bundle) {
    previousBackStackEntry?.savedStateHandle?.set(key, result)
}

@SuppressLint("ComposableNaming")
@Composable
fun NavController.observeResult(resultKey: String, onEvent: (Bundle) -> Unit) {
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    val savedStateFlow = savedStateHandle?.getStateFlow<Bundle?>(resultKey, null)
    val result = savedStateFlow?.collectAsStateWithLifecycle()

    LaunchedEffect(result?.value) {
        result?.value?.let { bundle ->
            savedStateHandle[resultKey] = null
            onEvent(bundle)
        }
    }
}