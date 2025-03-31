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

package com.blacksquircle.ui.feature.editor.ui.fragment.internal

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.editor.data.model.KeyModel

@Composable
internal fun ExtendedKeyboard(
    modifier: Modifier = Modifier,
    preset: List<KeyModel> = emptyList(),
    onKeyClick: (KeyModel) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(16.dp)
            .horizontalScroll(rememberScrollState())
            .background(SquircleTheme.colors.colorBackgroundSecondary)
            .navigationBarsPadding()
    ) {
        preset.fastForEach { key ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onKeyClick(key) }
            ) {
                Text(
                    text = key.display,
                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                    style = SquircleTheme.typography.text16Medium,
                    fontFamily = FontFamily(Typeface.MONOSPACE),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ExtendedKeyboardPreview() {
    PreviewBackground {
        ExtendedKeyboard(preset = emptyList())
    }
}