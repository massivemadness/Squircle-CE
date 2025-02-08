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

package com.blacksquircle.ui.feature.settings.ui.application

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.VerticalDivider
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.feature.settings.R

private val LeftHalfShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
    }
}

private val RightHalfShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(size.width / 2, 0f, size.width, size.height))
    }
}

@Composable
internal fun ThemePreference(
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        DarkTheme(
            title = stringResource(R.string.theme_dark),
            checked = selectedTheme == Theme.DARK,
            onClick = { onThemeSelected(Theme.DARK) }
        )
        LightTheme(
            title = stringResource(R.string.theme_light),
            checked = selectedTheme == Theme.LIGHT,
            onClick = { onThemeSelected(Theme.LIGHT) }
        )
        SystemTheme(
            title = stringResource(R.string.theme_auto),
            checked = selectedTheme == Theme.SYSTEM_DEFAULT,
            onClick = { onThemeSelected(Theme.SYSTEM_DEFAULT) }
        )
    }
}

@Composable
private fun DarkTheme(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String = "",
    checked: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        SquircleTheme(darkTheme = true) {
            BaseTheme(
                onClick = onClick,
                colorTop = SquircleTheme.colors.colorBackgroundSecondary,
                colorMiddle = SquircleTheme.colors.colorTextAndIconDisabled,
                colorBottom = SquircleTheme.colors.colorBackgroundTertiary,
            )
        }
        if (title.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Radio(
                title = title,
                checked = checked,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun LightTheme(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String = "",
    checked: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        SquircleTheme(darkTheme = false) {
            BaseTheme(
                onClick = onClick,
                colorTop = SquircleTheme.colors.colorBackgroundTertiary,
                colorMiddle = SquircleTheme.colors.colorTextAndIconDisabled,
                colorBottom = SquircleTheme.colors.colorBackgroundTertiary,
            )
        }
        if (title.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Radio(
                title = title,
                checked = checked,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun SystemTheme(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String = "",
    checked: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box {
            LightTheme(
                onClick = onClick,
                modifier = Modifier.graphicsLayer {
                    shape = LeftHalfShape
                    clip = true
                }
            )
            DarkTheme(
                onClick = onClick,
                modifier = Modifier.graphicsLayer {
                    shape = RightHalfShape
                    clip = true
                }
            )
        }
        if (title.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Radio(
                title = title,
                checked = checked,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun BaseTheme(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    colorBackground: Color = SquircleTheme.colors.colorBackgroundPrimary,
    colorOutline: Color = SquircleTheme.colors.colorOutline,
    colorPrimary: Color = SquircleTheme.colors.colorPrimary,
    colorTop: Color = SquircleTheme.colors.colorBackgroundSecondary,
    colorMiddle: Color = SquircleTheme.colors.colorTextAndIconDisabled,
    colorBottom: Color = SquircleTheme.colors.colorBackgroundTertiary,
    shapeLarge: Shape = RoundedCornerShape(12.dp),
    shapeMedium: Shape = RoundedCornerShape(8.dp),
    shapeSmall: Shape = RoundedCornerShape(4.dp),
) {
    Row(
        modifier
            .size(152.dp, 106.dp)
            .border(1.dp, colorOutline, shapeLarge)
            .background(colorBackground, shapeLarge)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick,
            )
    ) {
        Box(
            Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .size(width = 24.dp, height = 8.dp)
                .background(colorPrimary, shapeMedium)
        )

        VerticalDivider()

        Column {
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(colorTop, shapeSmall)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(0.8f)
                    .height(8.dp)
                    .background(colorMiddle, shapeMedium)
            )

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(0.6f)
                    .height(8.dp)
                    .background(colorBottom, shapeMedium)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(12.dp)
                    .size(24.dp)
                    .background(colorPrimary, shapeSmall)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ThemePreferencePreview() {
    PreviewBackground {
        ThemePreference(
            selectedTheme = Theme.DARK,
            onThemeSelected = {}
        )
    }
}