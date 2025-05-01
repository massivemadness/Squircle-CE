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

package com.blacksquircle.ui.filesystem.sftp

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.AuthRequiredException
import com.blacksquircle.ui.filesystem.base.exception.AuthenticationException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.hasFlag
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.ibm.icu.text.CharsetDetector
import com.jcraft.jsch.*
import com.jcraft.jsch.ChannelSftp.LsEntry
import kotlinx.coroutines.flow.Flow
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.Security
import java.util.*

class SFTPFilesystem(
    private val serverConfig: ServerConfig,
    private val cacheDir: File,
    private val keysDir: File,
) : Filesystem {

    private val jsch = JSch()
    private var session: Session? = null
    private var channel: ChannelSftp? = null

    private val sftpMapper = SFTPMapper()

    init {
        Security.insertProviderAt(BouncyCastleProvider(), 1)
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
            sftpMapper.parent(parent)
            return channel?.ls(parent.path).orEmpty()
                .filter { it.filename.isValidFileName() }
                .map(sftpMapper::toFileModel)
        } finally {
            disconnect()
        }
    }

    override fun createFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.isDirectory) {
                channel?.mkdir(fileModel.path)
            } else {
                channel?.put("".byteInputStream(), fileModel.path)
            }
        } finally {
            disconnect()
        }
    }

    override fun renameFile(source: FileModel, name: String) {
        try {
            connect()
            val base = source.path.substringBeforeLast(File.separator)
            channel?.rename(source.path, base + File.separator + name)
        } finally {
            disconnect()
        }
    }

    override fun deleteFile(fileModel: FileModel) {
        try {
            connect()
            if (fileModel.isDirectory) {
                channel?.rmdir(fileModel.path)
            } else {
                channel?.rm(fileModel.path)
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
                channel?.get(fileModel.path, it)
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
                channel?.put(it, fileModel.path)
            }
        } finally {
            tempFile.deleteRecursively()
            disconnect()
        }
    }

    private fun connect() {
        try {
            jsch.removeAllIdentity()
            session = jsch.getSession(
                serverConfig.username,
                serverConfig.address,
                serverConfig.port,
            ).apply {
                when (serverConfig.authMethod) {
                    AuthMethod.PASSWORD -> {
                        if (serverConfig.password == null) {
                            throw AuthRequiredException(AuthMethod.PASSWORD)
                        }
                        setPassword(serverConfig.password)
                    }
                    AuthMethod.KEY -> {
                        if (serverConfig.passphrase == null) {
                            throw AuthRequiredException(AuthMethod.KEY)
                        }
                        val keyId = serverConfig.keyId.orEmpty()
                        val keyFile = File(keysDir, keyId)
                        if (!keyFile.exists()) {
                            throw FileNotFoundException("Key with id $keyId not found")
                        }
                        val keyPath = keyFile.absolutePath
                        val keyPair = KeyPair.load(jsch, keyPath)

                        if (keyPair.isEncrypted) {
                            if (keyPair.decrypt(serverConfig.passphrase)) {
                                jsch.addIdentity(keyPath, serverConfig.passphrase)
                            } else {
                                throw AuthRequiredException(AuthMethod.KEY)
                            }
                        } else {
                            jsch.addIdentity(keyPath)
                        }
                    }
                }
                setConfig("StrictHostKeyChecking", "no")
                connect()
            }
            channel = session?.openChannel(CHANNEL_SFTP) as ChannelSftp
            channel?.connect()
        } catch (e: JSchException) {
            if (e.message.orEmpty().contains("Auth")) {
                throw AuthenticationException(serverConfig.authMethod)
            } else {
                throw e
            }
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

        @Suppress("KotlinConstantConditions")
        override fun toFileModel(fileObject: LsEntry): FileModel {
            return FileModel(
                fileUri = parent?.fileUri + File.separator + fileObject.filename,
                filesystemUuid = serverConfig.uuid,
                size = fileObject.attrs.size,
                lastModified = fileObject.attrs.mTime * 1000L,
                isDirectory = fileObject.attrs.isDir,
                permission = with(fileObject) {
                    var permission = Permission.EMPTY
                    if (attrs.permissions hasFlag (4 shl 2)) { // SftpATTRS.S_IRUSR
                        permission = permission plusFlag Permission.OWNER_READ
                    }
                    if (attrs.permissions hasFlag (2 shl 2)) { // SftpATTRS.S_IWUSR
                        permission = permission plusFlag Permission.OWNER_WRITE
                    }
                    if (attrs.permissions hasFlag (1 shl 2)) { // SftpATTRS.S_IXUSR
                        permission = permission plusFlag Permission.OWNER_EXECUTE
                    }
                    if (attrs.permissions hasFlag (4 shl 1)) { // SftpATTRS.S_IRGRP
                        permission = permission plusFlag Permission.GROUP_READ
                    }
                    if (attrs.permissions hasFlag (2 shl 1)) { // SftpATTRS.S_IWGRP
                        permission = permission plusFlag Permission.GROUP_WRITE
                    }
                    if (attrs.permissions hasFlag (1 shl 1)) { // SftpATTRS.S_IXGRP
                        permission = permission plusFlag Permission.GROUP_EXECUTE
                    }
                    if (attrs.permissions hasFlag 4) { // SftpATTRS.S_IROTH
                        permission = permission plusFlag Permission.OTHERS_READ
                    }
                    if (attrs.permissions hasFlag 2) { // SftpATTRS.S_IWOTH
                        permission = permission plusFlag Permission.OTHERS_WRITE
                    }
                    if (attrs.permissions hasFlag 1) { // SftpATTRS.S_IXOTH
                        permission = permission plusFlag Permission.OTHERS_EXECUTE
                    }
                    permission
                },
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
        private const val CHANNEL_SFTP = "sftp"
    }
}