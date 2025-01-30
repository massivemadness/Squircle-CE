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

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Colors as MaterialColors
import androidx.compose.material.darkColors as materialDarkColors
import androidx.compose.material.lightColors as materialLightColors

internal val LocalColors = staticCompositionLocalOf { Colors.darkColors() }

@Immutable
class Colors internal constructor(
    val colorPrimary: Color,
    val colorOutline: Color,
    val colorError: Color,
    val colorBackgroundPrimary: Color,
    val colorBackgroundSecondary: Color,
    val colorBackgroundTertiary: Color,
    val colorTextAndIconPrimary: Color,
    val colorTextAndIconSecondary: Color,
    val colorTextAndIconBrand: Color,
    val colorTextAndIconDisabled: Color,
) {

    companion object {

        fun lightColors() = darkColors() // TODO

        fun darkColors() = Colors(
            colorPrimary = Color(0xFFFF8000),
            colorOutline = Color(0xFF393B40),
            colorError = Color(0xFFE45356),
            colorBackgroundPrimary = Color(0xFF1E1F22),
            colorBackgroundSecondary = Color(0xFF2B2D30),
            colorBackgroundTertiary = Color(0xFFAAABAD),
            colorTextAndIconPrimary = Color(0xFFFFFFFF),
            colorTextAndIconSecondary = Color(0xFFBCBCBC),
            colorTextAndIconBrand = Color(0xFFFF8000),
            colorTextAndIconDisabled = Color(0xFF6E6E6E),
        )
    }

    @SuppressLint("ConflictingOnColor")
    fun toMaterialColors(darkTheme: Boolean): MaterialColors {
        return if (darkTheme) {
            materialDarkColors(
                primary = colorPrimary,
                primaryVariant = colorPrimary,
                secondary = colorPrimary,
                secondaryVariant = colorPrimary,
                background = colorBackgroundPrimary,
                surface = colorBackgroundSecondary,
                error = colorError,
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White,
                onError = Color.Black,
            )
        } else {
            materialLightColors(
                primary = colorPrimary,
                primaryVariant = colorPrimary,
                secondary = colorPrimary,
                secondaryVariant = colorPrimary,
                background = colorBackgroundPrimary,
                surface = colorBackgroundSecondary,
                error = colorError,
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color.Black,
                onSurface = Color.Black,
                onError = Color.White,
            )
        }
    }
}