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

import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.blacksquircle.ui.core.navigation.Screen

data class NavAction(val id: Int, val args: Bundle? = null)

fun NavController.navigateTo(
    screen: Any,
    options: NavOptions? = null,
    extras: Navigator.Extras? = null,
) {
    if (screen !is Screen) {
        throw IllegalArgumentException()
    }
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