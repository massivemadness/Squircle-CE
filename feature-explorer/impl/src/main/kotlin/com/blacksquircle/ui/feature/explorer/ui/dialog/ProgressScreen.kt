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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ProgressViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun ProgressScreen(viewModel: ProgressViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ProgressScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onCancelClicked = viewModel::onCancelClicked,
        onRunInBackgroundClicked = viewModel::onRunInBackgroundClicked,
    )
}

@Composable
private fun ProgressScreen(
    viewState: ProgressViewState,
    onBackClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onRunInBackgroundClicked: () -> Unit = {},
) {
    var elapsedMillis by rememberSaveable {
        mutableLongStateOf(0L)
    }
    LaunchedEffect(Unit) {
        while (isActive) {
            elapsedMillis = System.currentTimeMillis() - viewState.timestamp
            delay(1000L)
        }
    }

    AlertDialog(
        title = when (viewState.type) {
            TaskType.CREATE -> stringResource(R.string.dialog_title_creating)
            TaskType.RENAME -> stringResource(R.string.dialog_title_renaming)
            TaskType.DELETE -> stringResource(R.string.dialog_title_deleting)
            TaskType.CUT -> stringResource(R.string.dialog_title_copying)
            TaskType.COPY -> stringResource(R.string.dialog_title_copying)
            TaskType.COMPRESS -> stringResource(R.string.dialog_title_compressing)
            TaskType.EXTRACT -> stringResource(R.string.dialog_title_extracting)
        },
        content = {
            Column {
                val pattern = stringResource(R.string.progress_time_format)
                val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                val elapsedTime = stringResource(
                    R.string.message_elapsed_time,
                    formatter.format(elapsedMillis),
                )
                Text(
                    text = elapsedTime,
                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                    style = SquircleTheme.typography.text14Regular,
                )

                Spacer(Modifier.height(16.dp))

                if (viewState.details.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            when (viewState.type) {
                                TaskType.CREATE -> R.string.message_creating
                                TaskType.RENAME -> R.string.message_renaming
                                TaskType.DELETE -> R.string.message_deleting
                                TaskType.CUT -> R.string.message_copying
                                TaskType.COPY -> R.string.message_copying
                                TaskType.COMPRESS -> R.string.message_compressing
                                TaskType.EXTRACT -> R.string.message_extracting
                            },
                            viewState.details
                        ),
                        color = SquircleTheme.colors.colorTextAndIconSecondary,
                        style = SquircleTheme.typography.text14Regular,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (viewState.count > 0 && viewState.totalCount > 0) {
                        Text(
                            text = stringResource(
                                R.string.message_of_total,
                                viewState.count,
                                viewState.totalCount,
                            ),
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                            style = SquircleTheme.typography.text14Regular,
                        )

                        Spacer(Modifier.width(16.dp))
                    }

                    LinearProgress(
                        progress = viewState.count / viewState.totalCount.toFloat(),
                        indeterminate = viewState.count == -1 || viewState.totalCount == -1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = stringResource(R.string.action_run_in_background),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = onRunInBackgroundClicked,
        onDismissClicked = onCancelClicked,
        onDismiss = onBackClicked,
    )
}

@PreviewLightDark
@Composable
private fun ProgressScreenPreview() {
    PreviewBackground {
        ProgressScreen(
            viewState = ProgressViewState(
                type = TaskType.COMPRESS,
                count = 3,
                totalCount = 5,
                details = "/storage/emulated/0/Download/JavaScriptAPI.js",
                timestamp = System.currentTimeMillis() - 5000L,
            )
        )
    }
}