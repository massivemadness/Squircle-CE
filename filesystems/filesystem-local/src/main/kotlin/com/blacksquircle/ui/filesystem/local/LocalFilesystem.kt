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

package com.blacksquircle.ui.filesystem.local

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.*
import com.blacksquircle.ui.filesystem.base.model.*
import com.blacksquircle.ui.filesystem.base.utils.endsWith
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.ibm.icu.text.CharsetDetector
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("BlockingMethodInNonBlockingContext")
class LocalFilesystem(private val defaultLocation: File) : Filesystem {

    /** zip4j only supports these formats */
    private val supportedArchives = arrayOf(".zip", ".jar")

    override suspend fun defaultLocation(): FileModel {
        return suspendCoroutine { cont ->
            val fileModel = toFileModel(defaultLocation)
            if (defaultLocation.isDirectory) {
                cont.resume(fileModel)
            } else {
                cont.resumeWithException(DirectoryExpectedException())
            }
        }
    }

    override suspend fun provideDirectory(parent: FileModel): FileTree {
        return suspendCoroutine { cont ->
            val file = toFileObject(parent)
            if (file.isDirectory) {
                val children = file.listFiles().orEmpty()
                    .map(::toFileModel)
                    .toList()
                val fileTree = FileTree(parent, children)
                cont.resume(fileTree)
            } else {
                cont.resumeWithException(DirectoryExpectedException())
            }
        }
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        return suspendCoroutine { cont ->
            val file = File(fileModel.path)
            cont.resume(file.exists())
        }
    }

    override suspend fun createFile(fileModel: FileModel) {
        return suspendCoroutine { cont ->
            val file = toFileObject(fileModel)
            if (!file.exists()) {
                if (fileModel.directory) {
                    file.mkdirs()
                } else {
                    val parentFile = file.parentFile!!
                    if (!parentFile.exists()) {
                        parentFile.mkdirs()
                    }
                    file.createNewFile()
                }
                cont.resume(Unit)
            } else {
                cont.resumeWithException(FileAlreadyExistsException(fileModel.path))
            }
        }
    }

    override suspend fun renameFile(source: FileModel, dest: FileModel) {
        return suspendCoroutine { cont ->
            val originalFile = toFileObject(source)
            val renamedFile = toFileObject(dest)
            if (originalFile.exists()) {
                if (!renamedFile.exists()) {
                    originalFile.renameTo(renamedFile)
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(FileAlreadyExistsException(renamedFile.absolutePath))
                }
            } else {
                cont.resumeWithException(FileNotFoundException(source.path))
            }
        }
    }

    override suspend fun deleteFile(fileModel: FileModel) {
        return suspendCoroutine { cont ->
            val file = toFileObject(fileModel)
            if (file.exists()) {
                file.deleteRecursively()
                cont.resume(Unit)
            } else {
                cont.resumeWithException(FileNotFoundException(fileModel.path))
            }
        }
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel) {
        return suspendCoroutine { cont ->
            val directory = toFileObject(dest)
            val sourceFile = toFileObject(source)
            val destFile = File(directory, sourceFile.name)
            if (sourceFile.exists()) {
                if (!destFile.exists()) {
                    sourceFile.copyRecursively(destFile, overwrite = false)
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(FileAlreadyExistsException(dest.path))
                }
            } else {
                cont.resumeWithException(FileNotFoundException(source.path))
            }
        }
    }

    // TODO: Use ProgressMonitor
    override suspend fun compressFiles(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val destFile = toFileObject(dest)
            val archiveFile = ZipFile(destFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (!destFile.exists()) {
                for (fileModel in source) {
                    val sourceFile = toFileObject(fileModel)
                    if (sourceFile.exists()) {
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
                    } else {
                        throw FileNotFoundException(fileModel.path)
                    }
                }
                close()
            } else {
                throw FileAlreadyExistsException(destFile.absolutePath)
            }
        }
    }

    // TODO: Use ProgressMonitor
    override suspend fun extractFiles(source: FileModel, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val sourceFile = toFileObject(source)
            val archiveFile = ZipFile(sourceFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (sourceFile.exists()) {
                if (sourceFile.name.endsWith(supportedArchives)) {
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
                            close() // FIXME send() вызывается только 1 раз
                        }
                        archiveFile.isEncrypted -> throw EncryptedArchiveException(source.path)
                        archiveFile.isSplitArchive -> throw SplitArchiveException(source.path)
                        else -> throw InvalidArchiveException(source.path)
                    }
                } else {
                    throw UnsupportedArchiveException(source.path)
                }
            } else {
                throw FileNotFoundException(source.path)
            }
        }
    }

    override suspend fun loadFile(fileModel: FileModel, fileParams: FileParams): String {
        return suspendCoroutine { cont ->
            val file = File(fileModel.path)
            if (file.exists()) {
                val charset = if (fileParams.chardet) {
                    try {
                        val charsetMatch = CharsetDetector()
                            .setText(file.readBytes())
                            .detect()
                        charset(charsetMatch.name)
                    } catch (e: Exception) {
                        Charsets.UTF_8
                    }
                } else {
                    fileParams.charset
                }
                val text = file.readText(charset)
                cont.resume(text)
            } else {
                cont.resumeWithException(FileNotFoundException(fileModel.path))
            }
        }
    }

    override suspend fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        return suspendCoroutine { cont ->
            val file = File(fileModel.path)
            if (!file.exists()) {
                val parentFile = file.parentFile!!
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
                file.createNewFile()
            }
            file.writeText(fileParams.linebreak(text), fileParams.charset)
            cont.resume(Unit)
        }
    }

    companion object : Filesystem.Mapper<File> {

        const val LOCAL_UUID = "local"
        const val LOCAL_SCHEME = "file://"

        override fun toFileModel(fileObject: File): FileModel {
            return FileModel(
                fileUri = LOCAL_SCHEME + fileObject.path,
                filesystemUuid = LOCAL_UUID,
                size = fileObject.length(),
                lastModified = fileObject.lastModified(),
                directory = fileObject.isDirectory,
                permission = with(fileObject) {
                    var permission = Permission.NONE
                    if (fileObject.canRead())
                        permission = permission plusFlag Permission.READABLE
                    if (fileObject.canWrite())
                        permission = permission plusFlag Permission.WRITABLE
                    if (fileObject.canExecute())
                        permission = permission plusFlag Permission.EXECUTABLE
                    permission
                }
            )
        }

        override fun toFileObject(fileModel: FileModel): File {
            return File(fileModel.path)
        }
    }
}