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

package com.blacksquircle.ui.filesystem.ftpes

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

class FTPESFilesystem(
    private val serverConfig: ServerConfig,
    private val cacheLocation: File,
) : Filesystem {

    private val ftpesClient = FTPSClient(false)
    private val ftpesMapper = FTPESMapper()

    init {
        ftpesClient.connectTimeout = 10000
    }

    override fun defaultLocation(): FileModel {
        return FileModel(FTPES_SCHEME + serverConfig.initialDir, serverConfig.uuid)
    }

    override fun provideDirectory(parent: FileModel): FileTree {
        try {
            connect()
            ftpesClient.changeWorkingDirectory(parent.path)
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
                throw FileNotFoundException(parent.path)
            }
            return FileTree(
                parent = ftpesMapper.parent(parent),
                children = ftpesClient.listFiles(parent.path)
                    .filter { it.name.isValidFileName() }
                    .map(ftpesMapper::toFileModel),
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
                ftpesClient.makeDirectory(fileModel.path)
            } else {
                ftpesClient.storeFile(fileModel.path, "".byteInputStream())
            }
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun renameFile(source: FileModel, dest: FileModel) {
        try {
            connect()
            ftpesClient.rename(source.path, dest.path)
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
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
                ftpesClient.removeDirectory(fileModel.path)
            } else {
                ftpesClient.deleteFile(fileModel.path)
            }
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
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
                ftpesClient.retrieveFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
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
                ftpesClient.storeFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
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
        if (ftpesClient.isConnected) {
            return
        }
        ftpesClient.connect(serverConfig.address, serverConfig.port)
        if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
            throw ConnectionException()
        }
        if (serverConfig.authMethod != AuthMethod.PASSWORD) {
            throw UnsupportedOperationException()
        }
        ftpesClient.enterLocalPassiveMode()
        ftpesClient.login(serverConfig.username, serverConfig.password)
        if (!FTPReply.isPositiveCompletion(ftpesClient.replyCode)) {
            throw AuthenticationException(AuthMethod.PASSWORD, true)
        }
    }

    private fun disconnect() {
        if (ftpesClient.isConnected) {
            ftpesClient.logout()
            ftpesClient.disconnect()
        }
    }

    inner class FTPESMapper : Filesystem.Mapper<FTPFile> {

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
        const val FTPES_SCHEME = "ftpes://"
    }
}