package com.blacksquircle.ui.feature.explorer.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.Observer
import androidx.work.*
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.explorer.data.utils.toData
import com.blacksquircle.ui.feature.explorer.data.utils.toFileList
import com.blacksquircle.ui.feature.explorer.data.utils.toFileModel
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Named

@HiltWorker
class CompressFileWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dispatcherProvider: DispatcherProvider,
    @Named("Local") private val filesystem: Filesystem,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io()) {
            try {
                val fileList = inputData.toFileList()
                filesystem.compress(fileList.dropLast(1), fileList.last())
                    .onEach { fileModel ->
                        setProgress(fileModel.toData())
                        delay(20)
                    }
                    .collect()
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                Result.failure()
            }
        }
    }

    companion object {

        private const val TAG = "CompressFileWorker"
        private const val JOB_NAME = "compress-file"

        private const val CHANNEL_ID = "file-explorer"
        private const val NOTIFICATION_ID = 146
        private const val ERROR_ID = 147

        fun scheduleJob(context: Context, fileList: List<FileModel>) {
            val workRequest = OneTimeWorkRequestBuilder<CompressFileWorker>()
                .setInputData(fileList.toData())
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(JOB_NAME, ExistingWorkPolicy.KEEP, workRequest)
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
    }
}