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

package com.blacksquircle.ui.feature.themes.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksquircle.ui.core.extensions.createTypefaceFromPath
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.OutlinedButton
import com.blacksquircle.ui.ds.extensions.isColorDark
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.model.CodePreview
import com.blacksquircle.ui.feature.themes.domain.model.InternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun ThemeOverview(
    themeModel: ThemeModel,
    fontPath: String,
    codeSample: String,
    onSelectClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    val isDarkTheme = themeModel.colorScheme.backgroundColor.isColorDark()

    Column(
        modifier = modifier
            .clip(shape)
            .background(color = Color(themeModel.colorScheme.backgroundColor))
            .border(
                width = 1.dp,
                color = SquircleTheme.colors.colorOutline,
                shape = shape,
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = themeModel.name,
                color = if (isDarkTheme) Color.White else Color.Black,
                style = SquircleTheme.typography.text14Regular,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )
            // TODO popup menu
            if (themeModel.isExternal) {
                IconButton(
                    iconResId = UiR.drawable.ic_close,
                    iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
                    onClick = onRemoveClicked,
                )
            }
        }

        val context = LocalContext.current
        Text(
            text = codeSample,
            color = Color(themeModel.colorScheme.textColor),
            style = TextStyle(
                fontFamily = FontFamily(
                    typeface = context.createTypefaceFromPath(fontPath)
                ),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        ) {
            Icon(
                painter = painterResource(UiR.drawable.ic_person),
                contentDescription = null,
                tint = SquircleTheme.colors.colorTextAndIconSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = themeModel.author,
                color = if (isDarkTheme) Color.White else Color.Black,
                style = SquircleTheme.typography.text14Regular,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            OutlinedButton(
                text = stringResource(R.string.action_select),
                onClick = onSelectClicked,
            )
        }
    }
}

@Preview
@Composable
private fun ThemeOverviewPreview() {
    SquircleTheme {
        ThemeOverview(
            themeModel = InternalTheme.THEME_DARCULA.theme,
            fontPath = "file:///android_asset/fonts/droid_sans_mono.ttf",
            codeSample = CodePreview.HTML.codeSample,
            onSelectClicked = {},
            onRemoveClicked = {},
        )
    }
}