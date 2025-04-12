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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.blacksquircle.ui.ds.Colors
import com.blacksquircle.ui.feature.themes.api.model.AppTheme
import com.blacksquircle.ui.feature.themes.api.model.Theme

@Composable
@ReadOnlyComposable
internal fun parseColors(appTheme: AppTheme?): Colors {
    val isDarkTheme = isSystemInDarkTheme()
    val parentTheme = if (isDarkTheme) Colors.darkColors() else Colors.lightColors()
    if (appTheme == null) {
        return parentTheme
    }
    return Colors.dynamicColors(
        colorPrimary = appTheme.colorPrimary?.let(::Color) ?: parentTheme.colorPrimary,
        colorOutline = appTheme.colorOutline?.let(::Color) ?: parentTheme.colorOutline,
        colorSuccess = appTheme.colorSuccess?.let(::Color) ?: parentTheme.colorSuccess,
        colorError = appTheme.colorError?.let(::Color) ?: parentTheme.colorError,
        colorBackgroundPrimary = appTheme.colorBackgroundPrimary?.let(::Color)
            ?: parentTheme.colorBackgroundPrimary,
        colorBackgroundSecondary = appTheme.colorBackgroundSecondary?.let(::Color)
            ?: parentTheme.colorBackgroundSecondary,
        colorBackgroundTertiary = appTheme.colorBackgroundTertiary?.let(::Color)
            ?: parentTheme.colorBackgroundTertiary,
        colorTextAndIconPrimary = appTheme.colorTextAndIconPrimary?.let(::Color)
            ?: parentTheme.colorTextAndIconPrimary,
        colorTextAndIconPrimaryInverse = appTheme.colorTextAndIconPrimaryInverse?.let(::Color)
            ?: parentTheme.colorTextAndIconPrimaryInverse,
        colorTextAndIconSecondary = appTheme.colorTextAndIconSecondary?.let(::Color)
            ?: parentTheme.colorTextAndIconSecondary,
        colorTextAndIconDisabled = appTheme.colorTextAndIconDisabled?.let(::Color)
            ?: parentTheme.colorTextAndIconDisabled,
        colorTextAndIconAdditional = appTheme.colorTextAndIconAdditional?.let(::Color)
            ?: parentTheme.colorTextAndIconAdditional,
        isDark = when (appTheme.type) {
            Theme.LIGHT -> false
            Theme.DARK -> true
        }
    )
}