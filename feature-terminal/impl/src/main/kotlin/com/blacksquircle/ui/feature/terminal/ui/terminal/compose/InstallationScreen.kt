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

package com.blacksquircle.ui.feature.terminal.ui.terminal.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.feature.terminal.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun InstallationScreen(
    installProgress: Float,
    installError: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (installError != null) {
            EmptyView(
                iconResId = UiR.drawable.ic_file_error,
                title = stringResource(UiR.string.common_error_occurred),
                subtitle = installError,
            )
            return@Box
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(300.dp)
                .padding(horizontal = 24.dp)
        ) {
            LinearProgress(
                progress = installProgress,
                indeterminate = installProgress <= 0,
            )
            Text(
                text = stringResource(
                    R.string.terminal_installer_message_installing,
                    (installProgress * 100).fastRoundToInt()
                ),
                style = SquircleTheme.typography.text16Regular,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun InstallationScreenPreview() {
    PreviewBackground {
        InstallationScreen(
            installProgress = 0.5f,
            installError = null,
        )
    }
}