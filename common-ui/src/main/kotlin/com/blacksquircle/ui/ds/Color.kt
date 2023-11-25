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

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal fun colors(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFFF8000),
            onPrimary = Color(0xFF000000),
            primaryContainer = Color(0xFFFF8000),
            onPrimaryContainer = Color(0xFF000000),
            secondary = Color(0xFFFF8000),
            onSecondary = Color(0xFF000000),
            secondaryContainer = Color(0xFFFF8000),
            onSecondaryContainer = Color(0xFF000000),
            tertiary = Color(0xFFFF8000),
            onTertiary = Color(0xFF000000),
            tertiaryContainer = Color(0xFFFF8000),
            onTertiaryContainer = Color(0xFF000000),
            background = Color(0xFF1E1F22),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF2B2D30),
            onSurface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFF2B2D30),
            onSurfaceVariant = Color(0xFFFFFFFF),
            error = Color(0xFFE45356),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFE45356),
            onErrorContainer = Color(0xFFFFFFFF),
            outline = Color(0xFF393B40),
            outlineVariant = Color(0xFF393B40),
        )
    } else {
        lightColorScheme() // TODO
    }
}