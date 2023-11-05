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

package com.blacksquircle.ui.core.extensions

import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.blacksquircle.ui.core.navigation.Screen

fun NavController.navigate(
    screen: Screen<*>,
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
        is Int -> navigate(
            resId = screen.route,
            args = null,
            navOptions = options,
            navigatorExtras = extras
        )
        else -> throw IllegalArgumentException("Can't handle route type")
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> FragmentManager.fragment(@IdRes id: Int): T? {
    return findFragmentById(id) as? T
}