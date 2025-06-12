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

package com.blacksquircle.ui.feature.terminal.data.installer

import android.content.Context
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.feature.terminal.data.extensions.unzipTar
import com.blacksquircle.ui.feature.terminal.data.network.AlpineApi
import com.blacksquircle.ui.feature.terminal.domain.installer.RuntimeInstaller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

internal class AlpineInstaller(
    private val alpineApi: AlpineApi,
    private val context: Context,
) : RuntimeInstaller {

    override fun isInstalled(): Boolean {
        return false // TODO check binaries
    }

    override suspend fun install(): Flow<Float> {
        return flow {
            val alpineDir = Directories.alpineDir(context)
            val tmpDir = Directories.tmpDir(context)

            val outputFile = File(tmpDir, ARCHIVE_NAME)
            if (outputFile.exists()) {
                outputFile.deleteRecursively()
            }

            alpineApi.downloadFile(AlpineSource.DOWNLOAD_LINK).use { responseBody ->
                val contentLength = responseBody.contentLength()
                val inputStream = responseBody.byteStream()
                val outputStream = outputFile.outputStream()

                val buffer = ByteArray(8 * 1024) // 8 MB
                var bytesRead: Int
                var totalBytesRead = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    if (contentLength > 0) {
                        val progress = totalBytesRead.toFloat() / contentLength.toFloat()
                        emit(progress)
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
            }

            outputFile.unzipTar(alpineDir)
            outputFile.deleteRecursively()

            // TODO proot
            // TODO init-host.sh, init.sh
        }
    }

    companion object {
        private const val ARCHIVE_NAME = "alpine.tar.gz"
    }
}