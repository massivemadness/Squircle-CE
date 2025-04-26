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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.modifier.debounceSelectable
import com.blacksquircle.ui.ds.tabs.TabIndicator
import com.blacksquircle.ui.feature.editor.ui.editor.menu.CloseMenu
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun DocumentTab(
    name: String,
    modified: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onDocumentClicked: () -> Unit = {},
    onCloseClicked: () -> Unit = {},
    onCloseOthersClicked: () -> Unit = {},
    onCloseAllClicked: () -> Unit = {},
) {
    Box(modifier.width(IntrinsicSize.Max)) {
        var menuExpanded by rememberSaveable { mutableStateOf(false) }
        CloseMenu(
            expanded = menuExpanded,
            onDismiss = { menuExpanded = false },
            onCloseClicked = { menuExpanded = false; onCloseClicked() },
            onCloseOthersClicked = { menuExpanded = false; onCloseOthersClicked() },
            onCloseAllClicked = { menuExpanded = false; onCloseAllClicked() },
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(36.dp)
                .debounceSelectable(
                    selected = selected,
                    onClick = {
                        if (selected) {
                            menuExpanded = true
                        } else {
                            onDocumentClicked()
                        }
                    },
                    enabled = true,
                    role = Role.Tab,
                    interactionSource = null,
                    indication = ripple()
                )
                .padding(start = 12.dp)
        ) {
            Text(
                text = if (modified) "• $name" else name,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                style = SquircleTheme.typography.text14Bold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            IconButton(
                iconResId = UiR.drawable.ic_close,
                iconButtonStyle = IconButtonStyleDefaults.Secondary,
                onClick = onCloseClicked,
                iconButtonSize = IconButtonSizeDefaults.XXS,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        AnimatedVisibility(
            visible = selected,
            enter = slideInVertically(
                animationSpec = tween(),
                initialOffsetY = { it },
            ),
            exit = slideOutVertically(
                animationSpec = tween(),
                targetOffsetY = { it },
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clipToBounds()
        ) {
            TabIndicator()
        }
    }
}

@PreviewLightDark
@Composable
private fun DocumentTabPreview() {
    PreviewBackground {
        var selected by remember { mutableIntStateOf(0) }
        Row {
            DocumentTab(
                name = "untitled.txt",
                modified = false,
                selected = selected == 0,
                onDocumentClicked = { selected = 0 },
            )
            DocumentTab(
                name = "Document.txt",
                modified = true,
                selected = selected == 1,
                onDocumentClicked = { selected = 1 },
            )
        }
    }
}