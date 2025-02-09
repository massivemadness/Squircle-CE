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

package com.blacksquircle.ui.feature.explorer.data.factory

import android.os.Environment
import com.blacksquircle.ui.feature.explorer.api.factory.FilesystemFactory
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.model.FileServer
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import java.io.File

internal class FilesystemFactoryImpl(
    private val serversInteractor: ServersInteractor,
    private val cacheDirectory: File,
) : FilesystemFactory {

    override suspend fun create(uuid: String): Filesystem {
        return when (uuid) {
            LocalFilesystem.LOCAL_UUID -> LocalFilesystem(Environment.getExternalStorageDirectory())
            RootFilesystem.ROOT_UUID -> RootFilesystem()
            else -> {
                val serverConfig = serversInteractor.loadServer(uuid)
                return when (serverConfig.scheme) {
                    FileServer.FTP -> FTPFilesystem(serverConfig, cacheDirectory)
                    FileServer.FTPS -> FTPSFilesystem(serverConfig, cacheDirectory)
                    FileServer.FTPES -> FTPESFilesystem(serverConfig, cacheDirectory)
                    FileServer.SFTP -> SFTPFilesystem(serverConfig, cacheDirectory)
                }
            }
        }
    }
}