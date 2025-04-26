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

package com.blacksquircle.ui.feature.themes.ui.themes.compose

import android.graphics.Typeface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.button.OutlinedButton
import com.blacksquircle.ui.ds.extensions.isColorDark
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.model.EditorTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ThemeOverview(
    themeModel: ThemeModel,
    isSelected: Boolean,
    typeface: Typeface,
    onSelectClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    val backgroundColor = themeModel.colors.backgroundColor

    SquircleTheme(
        darkTheme = backgroundColor.isColorDark(),
        applySystemBars = false,
    ) {
        Column(
            modifier = modifier
                .clip(shape)
                .background(color = Color(backgroundColor))
                .border(
                    width = 1.dp,
                    color = SquircleTheme.colors.colorOutline,
                    shape = shape,
                )
                .mergeSemantics()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = themeModel.name,
                    color = SquircleTheme.colors.colorTextAndIconPrimary,
                    style = SquircleTheme.typography.text14Regular,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                if (themeModel.isExternal) {
                    IconButton(
                        iconResId = UiR.drawable.ic_close,
                        iconButtonStyle = IconButtonStyleDefaults.Secondary,
                        contentDescription = stringResource(R.string.action_remove),
                        onClick = onRemoveClicked,
                    )
                }
            }

            CodeView(
                colors = themeModel.colors,
                textStyle = TextStyle(
                    fontFamily = FontFamily(typeface),
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
            ) {
                Icon(
                    painter = painterResource(UiR.drawable.ic_person),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = themeModel.author,
                    color = SquircleTheme.colors.colorTextAndIconPrimary,
                    style = SquircleTheme.typography.text14Regular,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                OutlinedButton(
                    text = if (isSelected) {
                        stringResource(UiR.string.common_selected)
                    } else {
                        stringResource(UiR.string.common_select)
                    },
                    startIconResId = if (isSelected) {
                        UiR.drawable.ic_check
                    } else {
                        null
                    },
                    enabled = !isSelected,
                    onClick = onSelectClicked,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ThemeOverviewPreview() {
    PreviewBackground {
        ThemeOverview(
            themeModel = ThemeModel(
                uuid = "1",
                name = "Darcula",
                author = "Squircle CE",
                colors = EditorTheme.DARCULA,
                isExternal = true,
            ),
            isSelected = false,
            typeface = Typeface.MONOSPACE,
            onSelectClicked = {},
            onRemoveClicked = {},
        )
    }
}