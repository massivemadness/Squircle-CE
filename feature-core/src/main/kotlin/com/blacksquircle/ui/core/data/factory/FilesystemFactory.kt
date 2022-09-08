/*
 * Copyright 2022 Squircle CE contributors.
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
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem

class FilesystemFactory(private val database: AppDatabase) {

    suspend fun create(uuid: String?): Filesystem {
        val filesystemUuid = uuid ?: LocalFilesystem.LOCAL_UUID
        val persistent = database.serverDao().load(filesystemUuid)
        return when (filesystemUuid) {
            LocalFilesystem.LOCAL_UUID -> LocalFilesystem(Environment.getExternalStorageDirectory())
            persistent?.uuid -> when (persistent.scheme) {
                FTPFilesystem.FTP_SCHEME -> FTPFilesystem(ServerConverter.toModel(persistent))
                FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(ServerConverter.toModel(persistent))
                SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(ServerConverter.toModel(persistent))
                else -> throw IllegalArgumentException("Can't find filesystem")
            }
            else -> throw IllegalArgumentException("Can't find filesystem")
        }
    }

    suspend fun findForPosition(position: Int): Filesystem {
        return when (position) {
            0 -> LocalFilesystem(Environment.getExternalStorageDirectory())
            1 -> LocalFilesystem(Environment.getRootDirectory())
            else -> {
                val serverModel = database.serverDao().loadAll()
                    .map(ServerConverter::toModel)
                    .getOrNull(position - 2)
                    ?: throw IllegalArgumentException("Can't find filesystem")
                when (serverModel.scheme) {
                    FTPFilesystem.FTP_SCHEME -> FTPFilesystem(serverModel)
                    FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(serverModel)
                    SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(serverModel)
                    else -> throw IllegalArgumentException("Can't find filesystem")
                }
            }
        }
    }
}