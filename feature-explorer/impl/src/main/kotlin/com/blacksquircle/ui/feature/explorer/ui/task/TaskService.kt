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

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.blacksquircle.ui.core.extensions.buildNotification
import com.blacksquircle.ui.core.extensions.createChannel
import com.blacksquircle.ui.core.service.ComponentService
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskStatus
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.blacksquircle.ui.ds.R as UiR

internal class TaskService : ComponentService() {

    @Inject
    lateinit var taskManager: TaskManager

    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }

    override fun onCreate() {
        ExplorerComponent.buildOrGet(this).inject(this)
        super.onCreate()
        createChannel(
            channelId = CHANNEL_ID,
            channelName = R.string.explorer_channel_name,
            channelDescription = R.string.explorer_channel_description,
        )

        val serviceNotification = buildNotification(
            channelId = CHANNEL_ID,
            notificationTitle = getString(R.string.explorer_channel_name),
            notificationMessage = getString(R.string.explorer_service_message),
            ongoing = true,
            silent = true,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                serviceNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                serviceNotification,
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START_TASK -> {
                val taskId = intent.getStringExtra(ARG_TASK_ID).orEmpty()
                val notificationId = taskId.hashCode() and 0x7FFFFFFF
                taskManager.monitor(taskId)
                    .onEach { task ->
                        if (task.isFinished) {
                            notificationManager.cancel(notificationId)
                            if (taskManager.isIdle()) {
                                stopSelf()
                            }
                        } else {
                            notificationManager.notify(
                                notificationId,
                                createTaskNotification(task)
                            )
                        }
                    }
                    .launchIn(lifecycleScope)
            }

            ACTION_CANCEL_TASK -> {
                val taskId = intent.getStringExtra(ARG_TASK_ID).orEmpty()
                taskManager.cancel(taskId)
            }
        }
        return START_NOT_STICKY
    }

    private fun createTaskNotification(task: Task): Notification {
        val count = (task.status as? TaskStatus.Progress)
            ?.count ?: -1
        val totalCount = (task.status as? TaskStatus.Progress)
            ?.totalCount ?: -1
        val details = (task.status as? TaskStatus.Progress)
            ?.details

        return buildNotification(
            channelId = CHANNEL_ID,
            notificationTitle = when (task.type) {
                TaskType.CREATE -> getString(R.string.dialog_title_creating)
                TaskType.RENAME -> getString(R.string.dialog_title_renaming)
                TaskType.DELETE -> getString(R.string.dialog_title_deleting)
                TaskType.MOVE -> getString(R.string.dialog_title_copying)
                TaskType.COPY -> getString(R.string.dialog_title_copying)
                TaskType.COMPRESS -> getString(R.string.dialog_title_compressing)
                TaskType.EXTRACT -> getString(R.string.dialog_title_extracting)
                TaskType.CLONE -> getString(R.string.dialog_title_cloning)
            },
            notificationMessage = details,
            smallIcon = UiR.drawable.ic_file_clock,
            progress = count,
            progressMax = totalCount,
            indeterminate = count == -1 || totalCount == -1,
            ongoing = true,
            silent = true,
            actions = listOf(
                NotificationCompat.Action(
                    UiR.drawable.ic_close,
                    getString(android.R.string.cancel),
                    PendingIntent.getService(
                        this,
                        0,
                        Intent(this, TaskService::class.java).apply {
                            action = ACTION_CANCEL_TASK
                            putExtra(ARG_TASK_ID, task.id)
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    ),
                ),
            )
        )
    }

    companion object {

        const val ACTION_START_TASK = "com.blacksquircle.ui.ACTION_START_TASK"
        const val ACTION_CANCEL_TASK = "com.blacksquircle.ui.ACTION_CANCEL_TASK"
        const val ARG_TASK_ID = "ARG_TASK_ID"

        private const val CHANNEL_ID = "file-explorer"
        private const val NOTIFICATION_ID = 1
    }
}