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

package com.blacksquircle.ui.filesystem.sftp

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.FilesystemException
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.hasFlag
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SFTPFilesystem(
    private val serverModel: ServerModel,
    private val cacheLocation: File,
) : Filesystem {

    private val jsch = JSch()
    private var session: Session? = null
    private var channel: ChannelSftp? = null

    private val sftpMapper = SFTPMapper()

    override suspend fun defaultLocation(): FileModel {
        return FileModel(SFTP_SCHEME, serverModel.uuid)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun provideDirectory(parent: FileModel): FileTree {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
                val list = channel?.ls(parent.path)
                val fileTree = FileTree(
                    parent = sftpMapper.parent(parent),
                    children = (list as Vector<ChannelSftp.LsEntry>)
                        .filter { it.filename.isValidFileName() }
                        .map(sftpMapper::toFileModel)
                )
                cont.resume(fileTree)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        throw UnsupportedOperationException()
    }

    override suspend fun createFile(fileModel: FileModel) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun renameFile(source: FileModel, dest: FileModel) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun deleteFile(fileModel: FileModel) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel) {
        throw UnsupportedOperationException()
    }

    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override suspend fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        throw UnsupportedOperationException()
    }

    override suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
            } finally {
                disconnect()
            }
        }
    }

    override suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        return suspendCoroutine { cont ->
            try {
                connect(cont)
            } finally {
                disconnect()
            }
        }
    }

    private fun connect(continuation: Continuation<*>) {
        try {
            jsch.removeAllIdentity()
            session = jsch.getSession(
                serverModel.username,
                serverModel.address,
                serverModel.port,
            ).apply {
                when (serverModel.authMethod) {
                    AuthMethod.PASSWORD -> setPassword(serverModel.password)
                    AuthMethod.KEYSTORE -> TODO() // load private key
                }
                setConfig("StrictHostKeyChecking", "no")
                connect()
            }
            channel = session?.openChannel(CHANNEL_SFTP) as ChannelSftp
            channel?.connect()
        } catch (e: Exception) {
            continuation.resumeWithException(FilesystemException(e.message))
        }
    }

    private fun disconnect() {
        channel?.disconnect()
        session?.disconnect()
        channel = null
        session = null
    }

    inner class SFTPMapper : Filesystem.Mapper<LsEntry> {

        private var parent: FileModel? = null

        override fun toFileModel(fileObject: LsEntry): FileModel {
            return FileModel(
                fileUri = parent?.fileUri + "/" + fileObject.filename,
                filesystemUuid = serverModel.uuid,
                size = fileObject.attrs.size,
                lastModified = fileObject.attrs.mTime * 1000L,
                directory = fileObject.attrs.isDir,
                permission = with(fileObject) {
                    var permission = Permission.EMPTY
                    if (attrs.permissions hasFlag (4 shl 2)) // SftpATTRS.S_IRUSR
                        permission = permission plusFlag Permission.OWNER_READ
                    if (attrs.permissions hasFlag (2 shl 2)) // SftpATTRS.S_IWUSR
                        permission = permission plusFlag Permission.OWNER_WRITE
                    if (attrs.permissions hasFlag (1 shl 2)) // SftpATTRS.S_IXUSR
                        permission = permission plusFlag Permission.OWNER_EXECUTE
                    if (attrs.permissions hasFlag (4 shl 1)) // SftpATTRS.S_IRGRP
                        permission = permission plusFlag Permission.GROUP_READ
                    if (attrs.permissions hasFlag (2 shl 1)) // SftpATTRS.S_IWGRP
                        permission = permission plusFlag Permission.GROUP_WRITE
                    if (attrs.permissions hasFlag (1 shl 1)) // SftpATTRS.S_IXGRP
                        permission = permission plusFlag Permission.GROUP_EXECUTE
                    if (attrs.permissions hasFlag 4) // SftpATTRS.S_IROTH
                        permission = permission plusFlag Permission.OTHERS_READ
                    if (attrs.permissions hasFlag 2) // SftpATTRS.S_IWOTH
                        permission = permission plusFlag Permission.OTHERS_WRITE
                    if (attrs.permissions hasFlag 1) // SftpATTRS.S_IXOTH
                        permission = permission plusFlag Permission.OTHERS_EXECUTE
                    permission
                }
            )
        }

        override fun toFileObject(fileModel: FileModel): LsEntry {
            throw UnsupportedOperationException()
        }

        fun parent(parent: FileModel): FileModel {
            this.parent = parent
            return parent
        }
    }

    companion object {

        const val SFTP_SCHEME = "sftp://"

        private const val CHANNEL_SFTP = "sftp"
    }
}