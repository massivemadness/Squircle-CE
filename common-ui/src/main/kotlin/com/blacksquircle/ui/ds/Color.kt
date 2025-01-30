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

package com.blacksquircle.ui.ds

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

internal fun colors(darkTheme: Boolean): Colors {
    return if (darkTheme) {
        darkColors(
            primary = Color(0xFFFF8000),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFFF8000),
            onSecondary = Color(0xFFFFFFFF),
            background = Color(0xFF1E1F22),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF2B2D30),
            onSurface = Color(0xFFFFFFFF),
            error = Color(0xFFE45356),
            onError = Color(0xFFFFFFFF),
        )
    } else {
        lightColors() // TODO
    }
}