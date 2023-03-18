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

package com.blacksquircle.ui.filesystem.ftps

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.ConnectionException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import kotlinx.coroutines.flow.Flow
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import org.apache.commons.net.ftp.FTPSClient
import java.io.File
import java.util.*

class FTPSFilesystem(
    private val serverConfig: ServerConfig,
    private val cacheLocation: File,
) : Filesystem {

    private val ftpsClient = FTPSClient(true)
    private val ftpsMapper = FTPSMapper()

    init {
        ftpsClient.connectTimeout = 10000
    }

    override fun defaultLocation(): FileModel {
        return FileModel(FTPS_SCHEME + serverConfig.initialDir, serverConfig.uuid)
    }

    override fun provideDirectory(parent: FileModel): FileTree {
        try {
            connect()
            ftpsClient.changeWorkingDirectory(parent.path)
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(parent.path)
            }
            return FileTree(
                parent = ftpsMapper.parent(parent),
                children = ftpsClient.listFiles(parent.path)
                    .filter { it.name.isValidFileName() }
                    .map(ftpsMapper::toFileModel),
            )
        } finally {
            disconnect()
        }
    }

    override fun exists(fileModel: FileModel): Boolean {
        throw UnsupportedOperationException()
    }

    override fun createFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.directory) {
                ftpsClient.makeDirectory(fileModel.path)
            } else {
                ftpsClient.storeFile(fileModel.path, "".byteInputStream())
            }
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun renameFile(source: FileModel, dest: FileModel) {
        try {
            connect()
            ftpsClient.rename(source.path, dest.path)
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(source.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun deleteFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.directory) {
                ftpsClient.removeDirectory(fileModel.path)
            } else {
                ftpsClient.deleteFile(fileModel.path)
            }
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun copyFile(source: FileModel, dest: FileModel) {
        throw UnsupportedOperationException()
    }

    override fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        val tempFile = File(cacheLocation, UUID.randomUUID().toString())
        try {
            connect()

            tempFile.createNewFile()
            tempFile.outputStream().use {
                ftpsClient.retrieveFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
            return tempFile.readText(fileParams.charset)
        } finally {
            tempFile.deleteRecursively()
            disconnect()
        }
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        val tempFile = File(cacheLocation, UUID.randomUUID().toString())
        try {
            connect()

            tempFile.createNewFile()
            tempFile.writeText(text, fileParams.charset)
            tempFile.inputStream().use {
                ftpsClient.storeFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            tempFile.deleteRecursively()
            disconnect()
        }
    }

    private fun connect() {
        if (serverConfig.password == null) {
            throw AuthenticationException(AuthMethod.PASSWORD, false)
        }
        if (ftpsClient.isConnected) {
            return
        }
        ftpsClient.connect(serverConfig.address, serverConfig.port)
        if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
            throw ConnectionException()
        }
        if (serverConfig.authMethod != AuthMethod.PASSWORD) {
            throw UnsupportedOperationException()
        }
        ftpsClient.enterLocalPassiveMode()
        ftpsClient.login(serverConfig.username, serverConfig.password)
        if (!FTPReply.isPositiveCompletion(ftpsClient.replyCode)) {
            throw AuthenticationException(AuthMethod.PASSWORD, true)
        }
    }

    private fun disconnect() {
        if (ftpsClient.isConnected) {
            ftpsClient.logout()
            ftpsClient.disconnect()
        }
    }

    inner class FTPSMapper : Filesystem.Mapper<FTPFile> {

        private var parent: FileModel? = null

        override fun toFileModel(fileObject: FTPFile): FileModel {
            return FileModel(
                fileUri = parent?.fileUri + "/" + fileObject.name,
                filesystemUuid = serverConfig.uuid,
                size = fileObject.size,
                lastModified = fileObject.timestamp.timeInMillis,
                directory = fileObject.isDirectory,
                permission = with(fileObject) {
                    var permission = Permission.EMPTY
                    if (hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION)) {
                        permission = permission plusFlag Permission.OWNER_READ
                    }
                    if (hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION)) {
                        permission = permission plusFlag Permission.OWNER_WRITE
                    }
                    if (hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION)) {
                        permission = permission plusFlag Permission.OWNER_EXECUTE
                    }
                    if (hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION)) {
                        permission = permission plusFlag Permission.GROUP_READ
                    }
                    if (hasPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION)) {
                        permission = permission plusFlag Permission.GROUP_WRITE
                    }
                    if (hasPermission(FTPFile.GROUP_ACCESS, FTPFile.EXECUTE_PERMISSION)) {
                        permission = permission plusFlag Permission.GROUP_EXECUTE
                    }
                    if (hasPermission(FTPFile.WORLD_ACCESS, FTPFile.READ_PERMISSION)) {
                        permission = permission plusFlag Permission.OTHERS_READ
                    }
                    if (hasPermission(FTPFile.WORLD_ACCESS, FTPFile.WRITE_PERMISSION)) {
                        permission = permission plusFlag Permission.OTHERS_WRITE
                    }
                    if (hasPermission(FTPFile.WORLD_ACCESS, FTPFile.EXECUTE_PERMISSION)) {
                        permission = permission plusFlag Permission.OTHERS_EXECUTE
                    }
                    permission
                },
            )
        }

        override fun toFileObject(fileModel: FileModel): FTPFile {
            throw UnsupportedOperationException()
        }

        fun parent(parent: FileModel): FileModel {
            this.parent = parent
            return parent
        }
    }

    companion object {
        const val FTPS_SCHEME = "ftps://"
    }
}