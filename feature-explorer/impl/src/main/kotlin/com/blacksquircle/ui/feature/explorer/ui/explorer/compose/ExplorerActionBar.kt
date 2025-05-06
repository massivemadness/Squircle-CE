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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.navigationitem.NavigationItem
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ExplorerActionBar(
    taskType: TaskType,
    modifier: Modifier = Modifier,
    onRefreshClicked: () -> Unit = {},
    onCloneClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onClearBufferClicked: () -> Unit = {},
) {
    val hasBuffer = taskType == TaskType.MOVE ||
        taskType == TaskType.COPY

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SquircleTheme.colors.colorBackgroundSecondary)
            .navigationBarsPadding()
    ) {
        if (hasBuffer) {
            NavigationItem(
                iconResId = UiR.drawable.ic_autorenew,
                label = stringResource(R.string.action_refresh),
                onClick = onRefreshClicked,
                modifier = Modifier.weight(1f),
            )
            NavigationItem(
                iconResId = UiR.drawable.ic_paste,
                label = stringResource(R.string.action_paste),
                onClick = onPasteClicked,
                modifier = Modifier.weight(1f),
            )
            NavigationItem(
                iconResId = UiR.drawable.ic_close,
                label = stringResource(android.R.string.cancel),
                onClick = onClearBufferClicked,
                modifier = Modifier.weight(1f),
            )
        } else {
            NavigationItem(
                iconResId = UiR.drawable.ic_autorenew,
                label = stringResource(R.string.action_refresh),
                onClick = onRefreshClicked,
                modifier = Modifier.weight(1f),
            )
            NavigationItem(
                iconResId = UiR.drawable.ic_git,
                label = stringResource(R.string.action_clone),
                onClick = onCloneClicked,
                modifier = Modifier.weight(1f),
            )
            NavigationItem(
                iconResId = UiR.drawable.ic_plus,
                label = stringResource(R.string.action_create),
                onClick = onCreateClicked,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ExplorerActionBarPreview() {
    PreviewBackground {
        ExplorerActionBar(TaskType.CREATE)
    }
}