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

package com.blacksquircle.ui.feature.explorer.ui.task

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.contract.rememberNotificationContract
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.TaskDialog
import com.blacksquircle.ui.feature.explorer.data.utils.formatDate
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun TaskScreen(
    navArgs: TaskDialog,
    navController: NavController,
    viewModel: TaskViewModel = daggerViewModel { context ->
        val component = ExplorerComponent.buildOrGet(context)
        TaskViewModel.ParameterizedFactory(navArgs.taskId).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    TaskScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onCancelClicked = viewModel::onCancelClicked,
        onRunInBackgroundClicked = viewModel::onRunInBackgroundClicked,
    )

    val notificationContract = rememberNotificationContract { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> viewModel.onPermissionDenied()
            PermissionResult.GRANTED -> viewModel.onPermissionGranted()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is ExplorerViewEvent.StartService -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val notificationManager = context.getSystemService<NotificationManager>()
                        if (notificationManager?.areNotificationsEnabled() == false) {
                            notificationContract.launch(Manifest.permission.POST_NOTIFICATIONS)
                            return@collect
                        }
                    }

                    val intent = Intent(context, TaskService::class.java).apply {
                        action = TaskService.ACTION_START_TASK
                        putExtra(TaskService.ARG_TASK_ID, navArgs.taskId)
                    }
                    ContextCompat.startForegroundService(context, intent)

                    navController.popBackStack() // close dialog
                }
            }
        }
    }
}

@Composable
private fun TaskScreen(
    viewState: TaskViewState,
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
            TaskType.MOVE -> stringResource(R.string.dialog_title_copying)
            TaskType.COPY -> stringResource(R.string.dialog_title_copying)
            TaskType.COMPRESS -> stringResource(R.string.dialog_title_compressing)
            TaskType.EXTRACT -> stringResource(R.string.dialog_title_extracting)
            TaskType.CLONE -> stringResource(R.string.dialog_title_cloning)
        },
        content = {
            Column {
                val pattern = stringResource(R.string.progress_time_format)
                val elapsedTime = stringResource(
                    R.string.message_elapsed_time,
                    elapsedMillis.formatDate(pattern),
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
                                TaskType.MOVE -> R.string.message_copying
                                TaskType.COPY -> R.string.message_copying
                                TaskType.COMPRESS -> R.string.message_compressing
                                TaskType.EXTRACT -> R.string.message_extracting
                                TaskType.CLONE -> R.string.message_cloning
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
        TaskScreen(
            viewState = TaskViewState(
                type = TaskType.COMPRESS,
                count = 3,
                totalCount = 5,
                details = "/storage/emulated/0/Download/JavaScriptAPI.js",
                timestamp = System.currentTimeMillis() - 5000L,
            )
        )
    }
}