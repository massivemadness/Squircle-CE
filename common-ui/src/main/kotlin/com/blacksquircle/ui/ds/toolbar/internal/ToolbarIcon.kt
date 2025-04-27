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

package com.blacksquircle.ui.ds.toolbar.internal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.toolbar.ToolbarSize

@Composable
@NonRestartableComposable
internal fun ToolbarIcon(
    @DrawableRes iconRes: Int?,
    contentDescription: String?,
    onNavigationClicked: () -> Unit,
    toolbarSize: ToolbarSize,
    modifier: Modifier = Modifier,
) {
    if (iconRes != null) {
        IconButton(
            iconResId = iconRes,
            iconButtonStyle = IconButtonStyleDefaults.Primary,
            iconButtonSize = toolbarSize.iconButtonSize,
            contentDescription = contentDescription,
            onClick = onNavigationClicked,
            modifier = modifier.padding(toolbarSize.iconButtonPadding),
        )
    }
}