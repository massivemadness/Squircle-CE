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

package com.blacksquircle.ui.application.extensions

import androidx.compose.ui.graphics.Color
import com.blacksquircle.ui.ds.Colors
import com.blacksquircle.ui.feature.themes.api.model.ColorScheme
import com.blacksquircle.ui.feature.themes.api.model.ThemeType

internal fun toComposeColors(colorScheme: ColorScheme?): Colors {
    val defaultTheme = when (colorScheme?.type) {
        ThemeType.LIGHT -> Colors.lightColors()
        ThemeType.DARK -> Colors.darkColors()
        else -> Colors.darkColors()
    }
    if (colorScheme == null) {
        return defaultTheme
    }
    return Colors.dynamicColors(
        colorPrimary = colorScheme.colorPrimary?.let(::Color) ?: defaultTheme.colorPrimary,
        colorOutline = colorScheme.colorOutline?.let(::Color) ?: defaultTheme.colorOutline,
        colorBackgroundPrimary = colorScheme.colorBackgroundPrimary?.let(::Color)
            ?: defaultTheme.colorBackgroundPrimary,
        colorBackgroundSecondary = colorScheme.colorBackgroundSecondary?.let(::Color)
            ?: defaultTheme.colorBackgroundSecondary,
        colorBackgroundTertiary = colorScheme.colorBackgroundTertiary?.let(::Color)
            ?: defaultTheme.colorBackgroundTertiary,
        colorTextAndIconPrimary = colorScheme.colorTextAndIconPrimary?.let(::Color)
            ?: defaultTheme.colorTextAndIconPrimary,
        colorTextAndIconPrimaryInverse = colorScheme.colorTextAndIconPrimaryInverse?.let(::Color)
            ?: defaultTheme.colorTextAndIconPrimaryInverse,
        colorTextAndIconSecondary = colorScheme.colorTextAndIconSecondary?.let(::Color)
            ?: defaultTheme.colorTextAndIconSecondary,
        colorTextAndIconDisabled = colorScheme.colorTextAndIconDisabled?.let(::Color)
            ?: defaultTheme.colorTextAndIconDisabled,
        colorTextAndIconAdditional = colorScheme.colorTextAndIconAdditional?.let(::Color)
            ?: defaultTheme.colorTextAndIconAdditional,
        colorTextAndIconSuccess = colorScheme.colorTextAndIconSuccess?.let(::Color)
            ?: defaultTheme.colorTextAndIconSuccess,
        colorTextAndIconError = colorScheme.colorTextAndIconError?.let(::Color)
            ?: defaultTheme.colorTextAndIconError,
        isDark = colorScheme.type == ThemeType.DARK,
    )
}