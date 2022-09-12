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

package com.blacksquircle.ui.filesystem.ftp

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.ConnectionException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.*
import kotlinx.coroutines.flow.Flow
import org.apache.commons.net.ftp.*
import java.io.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("BlockingMethodInNonBlockingContext")
class FTPFilesystem(private val serverModel: ServerModel) : Filesystem {

    private val ftpClient = FTPClient()
    private val ftpMapper = FTPMapper()

    init {
        ftpClient.connectTimeout = 10000
    }

    override suspend fun defaultLocation(): FileModel {
        return FileModel(FTP_SCHEME, serverModel.uuid)
    }

    override suspend fun provideDirectory(parent: FileModel): FileTree {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
                ftpClient.changeWorkingDirectory(parent.path)
                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    cont.resumeWithException(FileNotFoundException(parent.path))
                }
                val fileTree = FileTree(
                    parent = ftpMapper.parent(parent),
                    children = ftpClient.listFiles(parent.path)
                        .filter { it.name != "." && it.name != ".." }
                        .map(ftpMapper::toFileModel)
                )
                cont.resume(fileTree)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun createFile(fileModel: FileModel) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
                if (fileModel.isFolder) {
                    ftpClient.makeDirectory(fileModel.path)
                    if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                        cont.resumeWithException(FileNotFoundException(fileModel.path))
                    }
                } else {
                    TODO("Not yet implemented")
                }
                cont.resume(Unit)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(fileModel: FileModel): FileModel {
        TODO("Not yet implemented")
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel) {
        TODO("Not yet implemented")
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        TODO("Not yet implemented")
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        TODO("Not yet implemented")
    }

    override suspend fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        TODO("Not yet implemented")
    }

    override suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
                val outputStream = object : OutputStream() {
                    private val stringBuilder = StringBuilder()
                    override fun toString() = stringBuilder.toString()
                    override fun write(byte: Int) {
                        stringBuilder.appendCodePoint(byte)
                    }
                }
                ftpClient.retrieveFile(fileModel.path, outputStream)
                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    cont.resumeWithException(FileNotFoundException(fileModel.path))
                }
                cont.resume(outputStream.toString())
                outputStream.close()
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
                val inputStream = text.byteInputStream(fileParams.charset)
                ftpClient.storeFile(fileModel.path, inputStream)
                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    cont.resumeWithException(FileNotFoundException(fileModel.path))
                }
                cont.resume(Unit)
                inputStream.close()
            } finally {
                disconnect()
            }
        }
    }

    private fun connect(continuation: Continuation<*>) {
        if (ftpClient.isConnected)
            return
        ftpClient.connect(serverModel.address, serverModel.port)
        if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
            continuation.resumeWithException(ConnectionException())
            return
        }
        ftpClient.enterLocalPassiveMode()
        ftpClient.login(serverModel.username, serverModel.password)
        if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
            continuation.resumeWithException(AuthenticationException())
            return
        }
    }

    private fun disconnect() {
        ftpClient.logout()
        ftpClient.disconnect()
    }

    inner class FTPMapper : Filesystem.Mapper<FTPFile> {

        private var parent: FileModel? = null

        override fun toFileModel(fileObject: FTPFile): FileModel {
            return FileModel(
                fileUri = parent?.fileUri + "/" + fileObject.name,
                filesystemUuid = serverModel.uuid,
                size = fileObject.size,
                lastModified = fileObject.timestamp.timeInMillis,
                isFolder = fileObject.isDirectory,
                isHidden = fileObject.name.startsWith("."),
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
        const val FTP_SCHEME = "ftp://"
    }
}