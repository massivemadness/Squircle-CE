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

package com.blacksquircle.ui.filesystem.ftp

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.ConnectionException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.ibm.icu.text.CharsetDetector
import kotlinx.coroutines.flow.Flow
import org.apache.commons.net.ftp.*
import java.io.*
import java.util.*

class FTPFilesystem(
    private val serverConfig: ServerConfig,
    private val cacheDir: File,
) : Filesystem {

    private val ftpClient = FTPClient()
    private val ftpMapper = FTPMapper()

    init {
        ftpClient.connectTimeout = 10000
    }

    override fun ping() {
        try {
            connect()
        } finally {
            disconnect()
        }
    }

    override fun listFiles(parent: FileModel): List<FileModel> {
        try {
            connect()
            ftpClient.changeWorkingDirectory(parent.path)
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw FileNotFoundException(parent.path)
            }
            ftpMapper.parent(parent)
            return ftpClient.listFiles(parent.path)
                .filter { it.name.isValidFileName() }
                .map(ftpMapper::toFileModel)
        } finally {
            disconnect()
        }
    }

    override fun createFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.isDirectory) {
                ftpClient.makeDirectory(fileModel.path)
            } else {
                ftpClient.storeFile(fileModel.path, "".byteInputStream())
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun renameFile(source: FileModel, name: String) {
        try {
            connect()
            val base = source.path.substringBeforeLast(File.separator)
            ftpClient.rename(source.path, base + File.separator + name)
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw FileNotFoundException(source.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun deleteFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.isDirectory) {
                ftpClient.removeDirectory(fileModel.path)
            } else {
                ftpClient.deleteFile(fileModel.path)
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
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
        val tempFile = File(cacheDir, UUID.randomUUID().toString())
        try {
            connect()

            tempFile.createNewFile()
            tempFile.outputStream().use {
                ftpClient.retrieveFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
            val charset = if (fileParams.chardet) {
                try {
                    val charsetMatch = CharsetDetector()
                        .setText(tempFile.inputStream())
                        .detect()
                    charset(charsetMatch.name)
                } catch (e: Exception) {
                    Charsets.UTF_8
                }
            } else {
                fileParams.charset
            }
            return tempFile.readText(charset)
        } finally {
            tempFile.deleteRecursively()
            disconnect()
        }
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        val tempFile = File(cacheDir, UUID.randomUUID().toString())
        try {
            connect()

            tempFile.createNewFile()
            tempFile.writeText(text, fileParams.charset)
            tempFile.inputStream().use {
                ftpClient.storeFile(fileModel.path, it)
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw FileNotFoundException(fileModel.path)
            }
        } finally {
            tempFile.deleteRecursively()
            disconnect()
        }
    }

    private fun connect() {
        if (serverConfig.password == null) {
            throw AuthRequiredException(AuthMethod.PASSWORD)
        }
        if (ftpClient.isConnected) {
            return
        }
        ftpClient.connect(serverConfig.address, serverConfig.port)
        if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
            throw ConnectionException()
        }
        if (serverConfig.authMethod != AuthMethod.PASSWORD) {
            throw UnsupportedOperationException()
        }
        ftpClient.enterLocalPassiveMode()
        ftpClient.login(serverConfig.username, serverConfig.password)
        if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
            throw AuthenticationException(AuthMethod.PASSWORD)
        }
    }

    private fun disconnect() {
        if (ftpClient.isConnected) {
            ftpClient.logout()
            ftpClient.disconnect()
        }
    }

    inner class FTPMapper : Filesystem.Mapper<FTPFile> {

        private var parent: FileModel? = null

        @Suppress("KotlinConstantConditions")
        override fun toFileModel(fileObject: FTPFile): FileModel {
            return FileModel(
                fileUri = parent?.fileUri + File.separator + fileObject.name,
                filesystemUuid = serverConfig.uuid,
                size = fileObject.size,
                lastModified = fileObject.timestamp.timeInMillis,
                isDirectory = fileObject.isDirectory,
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
}