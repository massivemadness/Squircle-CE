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

package com.blacksquircle.ui.feature.explorer.data.factory

import android.os.Environment
import com.blacksquircle.ui.feature.explorer.domain.factory.FilesystemFactory
import com.blacksquircle.ui.feature.servers.domain.repository.ServersRepository
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import java.io.File

class FilesystemFactoryImpl(
    private val serversRepository: ServersRepository,
    private val cacheDirectory: File,
) : FilesystemFactory {

    override suspend fun create(uuid: String): Filesystem {
        return when (uuid) {
            LocalFilesystem.LOCAL_UUID -> LocalFilesystem(Environment.getExternalStorageDirectory())
            RootFilesystem.ROOT_UUID -> RootFilesystem()
            else -> {
                val serverConfig = serversRepository.loadServer(uuid)
                return when (serverConfig.scheme) {
                    FTPFilesystem.FTP_SCHEME -> FTPFilesystem(serverConfig, cacheDirectory)
                    FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(serverConfig, cacheDirectory)
                    FTPESFilesystem.FTPES_SCHEME -> FTPESFilesystem(serverConfig, cacheDirectory)
                    SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(serverConfig, cacheDirectory)
                    else -> throw IllegalArgumentException("Unsupported file scheme")
                }
            }
        }
    }
}