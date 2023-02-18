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

package com.blacksquircle.ui.core.data.factory

import android.os.Environment
import com.blacksquircle.ui.core.data.converter.ServerConverter
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import java.io.File

class FilesystemFactory(
    private val database: AppDatabase,
    private val cacheDir: File,
) {

    suspend fun create(uuid: String): Filesystem {
        return when (uuid) {
            LOCAL_UUID -> LocalFilesystem(Environment.getExternalStorageDirectory())
            ROOT_UUID -> RootFilesystem()
            else -> {
                val serverEntity = database.serverDao().load(uuid)
                    ?: throw IllegalArgumentException("Can't find filesystem")
                val serverModel = ServerConverter.toModel(serverEntity)
                return when (serverModel.scheme) {
                    FTPFilesystem.FTP_SCHEME -> FTPFilesystem(serverModel, cacheDir)
                    FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(serverModel, cacheDir)
                    FTPESFilesystem.FTPES_SCHEME -> FTPESFilesystem(serverModel, cacheDir)
                    SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(serverModel, cacheDir)
                    else -> throw IllegalArgumentException("Unsupported file scheme")
                }
            }
        }
    }

    companion object {
        const val LOCAL_UUID = LocalFilesystem.LOCAL_UUID
        const val ROOT_UUID = RootFilesystem.ROOT_UUID
    }
}