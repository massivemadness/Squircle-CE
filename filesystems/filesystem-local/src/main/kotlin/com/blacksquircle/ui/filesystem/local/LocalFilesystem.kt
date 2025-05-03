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

package com.blacksquircle.ui.filesystem.local

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.*
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.endsWith
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.ibm.icu.text.CharsetDetector
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import java.io.File

class LocalFilesystem : Filesystem {

    /** zip4j only supports these formats */
    private val supportedArchives = arrayOf(".zip", ".jar")

    override fun ping() = Unit

    override fun listFiles(parent: FileModel): List<FileModel> {
        val file = toFileObject(parent)
        if (!file.isDirectory) {
            throw DirectoryExpectedException()
        }
        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }
        return file.listFiles().orEmpty()
            .map(::toFileModel)
    }

    override fun createFile(fileModel: FileModel) {
        val file = toFileObject(fileModel)
        if (file.exists()) {
            throw FileAlreadyExistsException(fileModel.path)
        }
        if (fileModel.isDirectory) {
            file.mkdirs()
        } else {
            val parentFile = file.parentFile!!
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }
    }

    override fun renameFile(source: FileModel, name: String) {
        val sourceFile = toFileObject(source)
        val destFile = File(sourceFile.parentFile, name)
        if (!sourceFile.exists()) {
            throw FileNotFoundException(sourceFile.path)
        }

        val sourcePath = sourceFile.absolutePath
        val destPath = destFile.absolutePath
        if (sourcePath == destPath) {
            throw FileAlreadyExistsException(destFile.absolutePath)
        }

        if (destFile.exists()) {
            if (!sourcePath.equals(destPath, ignoreCase = true)) {
                throw FileAlreadyExistsException(destFile.absolutePath)
            }
            val tempFile = File(sourceFile.parent, "temp_${System.currentTimeMillis()}.tmp")
            if (!sourceFile.renameTo(tempFile)) {
                throw RenameFileException(tempFile.absolutePath)
            }
            if (!tempFile.renameTo(destFile)) {
                throw RenameFileException(destFile.absolutePath)
            }
        } else {
            if (!sourceFile.renameTo(destFile)) {
                throw RenameFileException(destFile.absolutePath)
            }
        }
    }

    override fun deleteFile(fileModel: FileModel) {
        val file = toFileObject(fileModel)
        if (!file.exists()) {
            // throw FileNotFoundException(file.path)
            return
        }
        file.deleteRecursively()
    }

    override fun copyFile(source: FileModel, dest: FileModel) {
        val directory = toFileObject(dest)
        val sourceFile = toFileObject(source)
        val destFile = File(directory, sourceFile.name)
        if (!sourceFile.exists()) {
            throw FileNotFoundException(sourceFile.path)
        }
        if (destFile.exists()) {
            throw FileAlreadyExistsException(dest.path)
        }

        val sourcePath = sourceFile.canonicalPath
        val destPath = destFile.canonicalPath
        if (destPath.startsWith(sourcePath + File.separator)) {
            // Cannot copy a folder into itself
            throw UnsupportedOperationException()
        }
        sourceFile.copyRecursively(destFile, overwrite = false)
    }

    // TODO: Use ProgressMonitor
    override fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val destFile = toFileObject(dest)
            val archiveFile = ZipFile(destFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (destFile.exists()) {
                throw FileAlreadyExistsException(destFile.absolutePath)
            }
            for (fileModel in source) {
                val sourceFile = toFileObject(fileModel)
                if (!sourceFile.exists()) {
                    throw FileNotFoundException(sourceFile.path)
                }
                try {
                    if (sourceFile.isDirectory) {
                        archiveFile.addFolder(sourceFile)
                    } else {
                        archiveFile.addFile(sourceFile)
                    }
                } catch (e: ZipException) {
                    if (e.type == ZipException.Type.TASK_CANCELLED_EXCEPTION) {
                        throw CancellationException()
                    } else {
                        throw e
                    }
                }
                send(fileModel)
            }
            close()
        }
    }

    // TODO: Use ProgressMonitor
    override fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val sourceFile = toFileObject(source)
            val archiveFile = ZipFile(sourceFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (!sourceFile.exists()) {
                throw FileNotFoundException(sourceFile.path)
            }
            if (!sourceFile.name.endsWith(supportedArchives)) {
                throw UnsupportedArchiveException(source.path)
            }
            when {
                archiveFile.isValidZipFile -> {
                    try {
                        archiveFile.extractAll(dest.path)
                    } catch (e: ZipException) {
                        if (e.type == ZipException.Type.TASK_CANCELLED_EXCEPTION) {
                            throw CancellationException()
                        } else {
                            throw e
                        }
                    }
                    send(source)
                }
                archiveFile.isEncrypted -> throw EncryptedArchiveException(source.path)
                archiveFile.isSplitArchive -> throw SplitArchiveException(source.path)
                else -> throw InvalidArchiveException(source.path)
            }
            close() // FIXME send() вызывается только 1 раз
        }
    }

    override fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        val file = File(fileModel.path)
        if (!file.exists()) {
            throw FileNotFoundException(file.path)
        }
        val charset = if (fileParams.chardet) {
            try {
                val charsetMatch = CharsetDetector()
                    .setText(file.inputStream())
                    .detect()
                charset(charsetMatch.name)
            } catch (e: Exception) {
                Charsets.UTF_8
            }
        } else {
            fileParams.charset
        }
        return file.readText(charset)
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        val file = File(fileModel.path)
        if (!file.exists()) {
            val parentFile = file.parentFile!!
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }
        val output = text.replace(
            regex = fileParams.linebreak.regex,
            replacement = fileParams.linebreak.replacement,
        )
        file.writeText(output, fileParams.charset)
    }

    companion object : Filesystem.Mapper<File> {

        const val LOCAL_UUID = "local"
        const val LOCAL_SCHEME = "file://"

        @Suppress("KotlinConstantConditions")
        override fun toFileModel(fileObject: File): FileModel {
            return FileModel(
                fileUri = LOCAL_SCHEME + fileObject.path,
                filesystemUuid = LOCAL_UUID,
                size = fileObject.length(),
                lastModified = fileObject.lastModified(),
                isDirectory = fileObject.isDirectory,
                permission = with(fileObject) {
                    var permission = Permission.EMPTY
                    if (fileObject.canRead()) {
                        permission = permission plusFlag Permission.OWNER_READ
                    }
                    if (fileObject.canWrite()) {
                        permission = permission plusFlag Permission.OWNER_WRITE
                    }
                    if (fileObject.canExecute()) {
                        permission = permission plusFlag Permission.OWNER_EXECUTE
                    }
                    permission
                },
            )
        }

        override fun toFileObject(fileModel: FileModel): File {
            return File(fileModel.path)
        }
    }
}