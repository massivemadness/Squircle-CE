/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.ds.R as UiR

private const val TypeIcon = "type_icon"
private const val TypeKey = "type_key"
private const val ItemOptions = "key_options"
private const val ItemSaveFile = "key_save_file"
private const val ItemReadOnly = "key_read_only"
private const val ItemEditUndo = "key_edit_undo"
private const val ItemEditRedo = "key_edit_redo"
private const val ItemEditTab = "key_edit_tab"
private const val ItemKey = "key_"

@Composable
internal fun ExtendedKeyboard(
    currentDocument: DocumentState?,
    preset: List<Char>,
    showExtraKeys: Boolean,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    onExtraKeyClicked: (Char) -> Unit = {},
    onExtraOptionsClicked: () -> Unit = {},
    onSaveFileClicked: () -> Unit = {},
    onReadOnlyClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onRedoClicked: () -> Unit = {},
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(6.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp)
            .background(SquircleTheme.colors.colorBackgroundSecondary)
            .navigationBarsPadding()
    ) {
        item(
            key = ItemOptions,
            contentType = TypeIcon,
        ) {
            ExtraKey(
                iconResId = if (showExtraKeys) {
                    UiR.drawable.ic_arrow_left
                } else {
                    UiR.drawable.ic_arrow_right
                },
                text = stringResource(UiR.string.common_menu),
                debounce = true,
                onClick = onExtraOptionsClicked,
                modifier = Modifier.animateItem(),
            )
        }
        if (showExtraKeys) {
            item(
                key = ItemSaveFile,
                contentType = TypeIcon,
            ) {
                ExtraKey(
                    iconResId = UiR.drawable.ic_save,
                    text = stringResource(UiR.string.common_save),
                    debounce = true,
                    onClick = onSaveFileClicked,
                    modifier = Modifier.animateItem(),
                )
            }
            item(
                key = ItemReadOnly,
                contentType = TypeIcon,
            ) {
                ExtraKey(
                    iconResId = if (readOnly) {
                        UiR.drawable.ic_pencil
                    } else {
                        UiR.drawable.ic_eye
                    },
                    text = stringResource(R.string.editor_menu_file_read_only),
                    debounce = true,
                    onClick = onReadOnlyClicked,
                    modifier = Modifier.animateItem(),
                )
            }
            item(
                key = ItemEditUndo,
                contentType = TypeIcon,
            ) {
                ExtraKey(
                    iconResId = UiR.drawable.ic_undo,
                    text = stringResource(R.string.editor_menu_edit_undo),
                    debounce = false,
                    enabled = currentDocument?.canUndo ?: false,
                    onClick = onUndoClicked,
                    modifier = Modifier.animateItem(),
                )
            }
            item(
                key = ItemEditRedo,
                contentType = TypeIcon,
            ) {
                ExtraKey(
                    iconResId = UiR.drawable.ic_redo,
                    text = stringResource(R.string.editor_menu_edit_redo),
                    debounce = false,
                    enabled = currentDocument?.canRedo ?: false,
                    onClick = onRedoClicked,
                    modifier = Modifier.animateItem(),
                )
            }
        }
        item(
            key = ItemEditTab,
            contentType = TypeIcon,
        ) {
            ExtraKey(
                text = stringResource(UiR.string.common_tab),
                onClick = { onExtraKeyClicked('\t') },
                modifier = Modifier.animateItem(),
            )
        }
        items(
            items = preset,
            key = { ItemKey + it },
            contentType = { TypeKey },
        ) { char ->
            ExtraKey(
                text = char.toString(),
                onClick = { onExtraKeyClicked(char) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun ExtraKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    debounce: Boolean = false,
    enabled: Boolean = true,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp, 32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SquircleTheme.colors.colorBackgroundTertiary)
            .debounceClickable(
                enabled = enabled,
                debounce = debounce,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            )
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = text,
                tint = if (enabled) {
                    SquircleTheme.colors.colorTextAndIconSecondary
                } else {
                    SquircleTheme.colors.colorTextAndIconDisabled
                },
                modifier = modifier.size(20.dp),
            )
        } else {
            Text(
                text = text,
                color = if (enabled) {
                    SquircleTheme.colors.colorTextAndIconSecondary
                } else {
                    SquircleTheme.colors.colorTextAndIconDisabled
                },
                style = if (text.length > 1) {
                    SquircleTheme.typography.text14Medium
                } else {
                    SquircleTheme.typography.text16Medium
                },
                fontFamily = FontFamily(Typeface.MONOSPACE),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ExtendedKeyboardPreview() {
    PreviewBackground {
        ExtendedKeyboard(
            currentDocument = null,
            preset = "{}();,.=|&![]<>+-/*?:_".map { it },
            showExtraKeys = true,
            readOnly = false,
        )
    }
}