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

import android.content.Context
import android.os.Environment
import com.blacksquircle.ui.core.data.converter.ServerConverter
import com.blacksquircle.ui.core.data.storage.database.AppDatabase
import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile

class FilesystemFactory(
    private val database: AppDatabase,
    private val context: Context,
) {

    init {
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR)
                .setContext(context)
        )
    }

    suspend fun create(uuid: String?): Filesystem {
        val filesystemUuid = uuid ?: LocalFilesystem.LOCAL_UUID
        val persistent = database.serverDao().load(filesystemUuid)
        return when (filesystemUuid) {
            LocalFilesystem.LOCAL_UUID -> LocalFilesystem(defaultLocation())
            persistent?.uuid -> when (persistent.scheme) {
                FTPFilesystem.FTP_SCHEME -> FTPFilesystem(ServerConverter.toModel(persistent), cacheLocation())
                FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(ServerConverter.toModel(persistent), cacheLocation())
                FTPESFilesystem.FTPES_SCHEME -> FTPESFilesystem(ServerConverter.toModel(persistent), cacheLocation())
                SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(ServerConverter.toModel(persistent), cacheLocation())
                else -> throw IllegalArgumentException("Unsupported file scheme")
            }
            else -> throw IllegalArgumentException("Can't find filesystem")
        }
    }

    suspend fun findForPosition(position: Int): Filesystem {
        return when (position) {
            LOCAL -> LocalFilesystem(defaultLocation()) // Local Storage
            ROOT -> LocalFilesystem(rootLocation()) // Root Directory
            else -> { // Server List
                val serverModel = database.serverDao().loadAll()
                    .map(ServerConverter::toModel)
                    .getOrNull(position - 2) // 0 = local, 1 = root, 2..3.. - servers
                    ?: throw IllegalArgumentException("Can't find filesystem")
                when (serverModel.scheme) {
                    FTPFilesystem.FTP_SCHEME -> FTPFilesystem(serverModel, cacheLocation())
                    FTPSFilesystem.FTPS_SCHEME -> FTPSFilesystem(serverModel, cacheLocation())
                    FTPESFilesystem.FTPES_SCHEME -> FTPESFilesystem(serverModel, cacheLocation())
                    SFTPFilesystem.SFTP_SCHEME -> SFTPFilesystem(serverModel, cacheLocation())
                    else -> throw IllegalArgumentException("Unsupported file scheme")
                }
            }
        }
    }

    private fun defaultLocation() = Environment.getExternalStorageDirectory()
    private fun rootLocation() = SuFile("/")
    private fun cacheLocation() = context.cacheDir

    companion object {
        const val LOCAL = 0
        const val ROOT = 1
    }
}