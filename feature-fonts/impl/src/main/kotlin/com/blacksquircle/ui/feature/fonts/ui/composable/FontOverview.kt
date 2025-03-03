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

package com.blacksquircle.ui.feature.fonts.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksquircle.ui.core.extensions.createTypefaceFromPath
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.OutlinedButton
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.api.model.FontModel
import com.blacksquircle.ui.feature.fonts.api.model.InternalFont
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun FontOverview(
    fontModel: FontModel,
    isSelected: Boolean,
    onSelectClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.mergeSemantics()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = fontModel.name,
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text14Regular,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )
            if (fontModel.isExternal) {
                IconButton(
                    iconResId = UiR.drawable.ic_close,
                    iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
                    onClick = onRemoveClicked,
                )
            }
        }

        val context = LocalContext.current
        Text(
            text = stringResource(R.string.font_panagram),
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            style = TextStyle(
                fontFamily = FontFamily(
                    typeface = context.createTypefaceFromPath(fontModel.path)
                ),
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
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
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End)
        )
        HorizontalDivider()
    }
}

@PreviewLightDark
@Composable
private fun FontOverviewPreview() {
    PreviewBackground {
        FontOverview(
            fontModel = InternalFont.DROID_SANS_MONO.font,
            isSelected = false,
            onSelectClicked = {},
            onRemoveClicked = {},
        )
    }
}