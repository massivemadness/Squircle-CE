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

package com.blacksquircle.ui.feature.explorer.ui.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.Observer
import androidx.work.*
import com.blacksquircle.ui.core.extensions.buildNotification
import com.blacksquircle.ui.core.extensions.createChannel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.toData
import com.blacksquircle.ui.feature.explorer.data.utils.toFileList
import com.blacksquircle.ui.feature.explorer.data.utils.toFileModel
import com.blacksquircle.ui.feature.explorer.domain.factory.FilesystemFactory
import com.blacksquircle.ui.filesystem.base.exception.FileAlreadyExistsException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import com.blacksquircle.ui.uikit.R as UiR

@HiltWorker
class RenameFileWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dispatcherProvider: DispatcherProvider,
    private val filesystemFactory: FilesystemFactory,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io()) {
            setForeground(getForegroundInfo())
            try {
                val fileList = inputData.toFileList()
                val fileModel = fileList.first()
                setProgress(fileModel.toData())

                val filesystem = filesystemFactory.create(fileModel.filesystemUuid)
                filesystem.renameFile(fileList.first(), fileList.last())
                delay(20)

                withContext(dispatcherProvider.mainThread()) {
                    applicationContext.showToast(R.string.message_done)
                }
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                withContext(dispatcherProvider.mainThread()) {
                    when (e) {
                        is FileNotFoundException -> {
                            applicationContext.showToast(R.string.message_file_not_found)
                        }
                        is FileAlreadyExistsException -> {
                            applicationContext.showToast(R.string.message_file_already_exists)
                        }
                        is CancellationException -> {
                            applicationContext.showToast(R.string.message_operation_cancelled)
                        }
                        is UnsupportedOperationException -> {
                            applicationContext.showToast(R.string.message_operation_not_supported)
                        }
                        else -> {
                            applicationContext.showToast(text = e.message.toString())
                        }
                    }
                }
                Result.failure()
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        applicationContext.createChannel(
            channelId = CHANNEL_ID,
            channelName = R.string.explorer_channel_name,
            channelDescription = R.string.explorer_channel_description,
        )

        val notification = applicationContext.buildNotification(
            channelId = CHANNEL_ID,
            notificationTitle = applicationContext.getString(R.string.dialog_title_renaming),
            smallIcon = UiR.drawable.ic_file_clock,
            indeterminate = true,
            ongoing = true,
            silent = true,
            actions = listOf(
                NotificationCompat.Action(
                    UiR.drawable.ic_close,
                    applicationContext.getString(android.R.string.cancel),
                    WorkManager.getInstance(applicationContext)
                        .createCancelPendingIntent(id),
                ),
            ),
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    companion object {

        private const val JOB_NAME = "rename-file"
        private const val CHANNEL_ID = "file-explorer"
        private const val NOTIFICATION_ID = 141

        fun scheduleJob(context: Context, fileList: List<FileModel>) {
            val workRequest = OneTimeWorkRequestBuilder<RenameFileWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(fileList.toData())
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(JOB_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, workRequest)
        }

        fun observeJob(context: Context): Flow<FileModel> {
            return callbackFlow {
                val workManager = WorkManager.getInstance(context)
                val workInfoLiveData = workManager.getWorkInfosForUniqueWorkLiveData(JOB_NAME)
                val observer = Observer<List<WorkInfo>> { workInfos ->
                    val workInfo = workInfos.findLast { !it.state.isFinished }
                    if (workInfo != null) {
                        trySend(workInfo.progress.toFileModel())
                    } else {
                        close(ClosedSendChannelException("Channel was closed"))
                    }
                }
                workInfoLiveData.observeForever(observer)
                awaitClose { workInfoLiveData.removeObserver(observer) }
            }
        }

        fun cancelJob(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(JOB_NAME)
        }
    }
}