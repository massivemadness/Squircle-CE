/*
 * Copyright 2021 Squircle IDE contributors.
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
import com.blacksquircle.ui.filesystem.local.converter.FileConverter
import com.blacksquircle.ui.filesystem.local.utils.size
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

@Suppress("BlockingMethodInNonBlockingContext")
class LocalFilesystem(private val defaultLocation: File) : Filesystem {

    companion object {

        /**
         * zip4j only supports these formats
         */
        private val SUPPORTED_ARCHIVES = arrayOf(".zip", ".jar")
    }

    override suspend fun defaultLocation(): FileModel {
        return suspendCoroutine { cont ->
            val fileModel = FileConverter.toModel(defaultLocation)
            if (defaultLocation.isDirectory) {
                cont.resume(fileModel)
            } else {
                cont.resumeWithException(DirectoryExpectedException())
            }
        }
    }

    override suspend fun provideFile(path: String): FileModel {
        return suspendCoroutine { cont ->
            val file = File(path)
            if (file.exists()) {
                val fileModel = FileConverter.toModel(file)
                cont.resume(fileModel)
            } else {
                cont.resumeWithException(FileNotFoundException(file.path))
            }
        }
    }

    override suspend fun provideDirectory(parent: FileModel): FileTree {
        return suspendCoroutine { cont ->
            val file = FileConverter.toFile(parent)
            if (file.isDirectory) {
                val children = file.listFiles()!!
                    .map(FileConverter::toModel)
                    .toList()
                val fileTree = FileTree(parent, children)
                cont.resume(fileTree)
            } else {
                cont.resumeWithException(DirectoryExpectedException())
            }
        }
    }

    override suspend fun createFile(fileModel: FileModel): FileModel {
        return suspendCoroutine { cont ->
            val file = FileConverter.toFile(fileModel)
            if (!file.exists()) {
                if (fileModel.isFolder) {
                    file.mkdirs()
                } else {
                    val parentFile = file.parentFile!!
                    if (!parentFile.exists()) {
                        parentFile.mkdirs()
                    }
                    file.createNewFile()
                }
                val fileModel2 = FileConverter.toModel(file)
                cont.resume(fileModel2)
            } else {
                cont.resumeWithException(FileAlreadyExistsException(fileModel.path))
            }
        }
    }

    override suspend fun renameFile(fileModel: FileModel, fileName: String): FileModel {
        return suspendCoroutine { cont ->
            val originalFile = FileConverter.toFile(fileModel)
            val parentFile = originalFile.parentFile!!
            val renamedFile = File(parentFile, fileName)
            if (originalFile.exists()) {
                if (!renamedFile.exists()) {
                    originalFile.renameTo(renamedFile)
                    val renamedModel = FileConverter.toModel(renamedFile)
                    cont.resume(renamedModel)
                } else {
                    cont.resumeWithException(FileAlreadyExistsException(renamedFile.absolutePath))
                }
            } else {
                cont.resumeWithException(FileNotFoundException(fileModel.path))
            }
        }
    }

    override suspend fun deleteFile(fileModel: FileModel): FileModel {
        return suspendCoroutine { cont ->
            val file = FileConverter.toFile(fileModel)
            if (file.exists()) {
                file.deleteRecursively()
                val parentFile = FileConverter.toModel(file.parentFile!!)
                cont.resume(parentFile)
            } else {
                cont.resumeWithException(FileNotFoundException(fileModel.path))
            }
        }
    }

    override suspend fun copyFile(source: FileModel, dest: FileModel): FileModel {
        return suspendCoroutine { cont ->
            val directory = FileConverter.toFile(dest)
            val sourceFile = FileConverter.toFile(source)
            val destFile = File(directory, sourceFile.name)
            if (sourceFile.exists()) {
                if (!destFile.exists()) {
                    sourceFile.copyRecursively(destFile, overwrite = false)
                    // val destFile2 = FileConverter.toModel(destFile)
                    // emitter.onSuccess(destFile2)
                    cont.resume(source)
                } else {
                    cont.resumeWithException(FileAlreadyExistsException(dest.path))
                }
            } else {
                cont.resumeWithException(FileNotFoundException(source.path))
            }
        }
    }

    override suspend fun propertiesOf(fileModel: FileModel): PropertiesModel {
        return suspendCoroutine { cont ->
            val file = File(fileModel.path)
            val fileType = fileModel.getType()
            if (file.exists()) {
                val result = PropertiesModel(
                    file.name,
                    file.absolutePath,
                    file.lastModified(),
                    file.size(),
                    getLineCount(file, fileType),
                    getWordCount(file, fileType),
                    getCharCount(file, fileType),
                    file.canRead(),
                    file.canWrite(),
                    file.canExecute()
                )
                cont.resume(result)
            } else {
                cont.resumeWithException(FileNotFoundException(fileModel.path))
            }
        }
    }

    override suspend fun isExists(fileModel: FileModel): Boolean {
        return suspendCoroutine { cont ->
            val file = File(fileModel.path)
            cont.resume(file.exists())
        }
    }

    // TODO: Use ProgressMonitor
    @ExperimentalCoroutinesApi
    override suspend fun compress(source: List<FileModel>, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val destFile = FileConverter.toFile(dest)
            val archiveFile = ZipFile(destFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (!destFile.exists()) {
                for (fileModel in source) {
                    val sourceFile = FileConverter.toFile(fileModel)
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
                        offer(fileModel)
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
    @ExperimentalCoroutinesApi
    override suspend fun extractAll(source: FileModel, dest: FileModel): Flow<FileModel> {
        return callbackFlow {
            val sourceFile = FileConverter.toFile(source)
            val archiveFile = ZipFile(sourceFile)
            invokeOnClose {
                archiveFile.progressMonitor.isCancelAllTasks = true
            }
            if (sourceFile.exists()) {
                if (sourceFile.name.endsWith(SUPPORTED_ARCHIVES)) {
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
                            offer(source)
                            close() // FIXME offer() вызывается только 1 раз
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

    // region PROPERTIES

    private fun getLineCount(file: File, fileType: FileType): Int? {
        if (file.isFile && fileType == FileType.TEXT) {
            var lines = 0
            file.forEachLine {
                lines++
            }
            return lines
        }
        return null
    }

    private fun getWordCount(file: File, fileType: FileType): Int? {
        if (file.isFile && fileType == FileType.TEXT) {
            var words = 0
            file.forEachLine {
                words += it.split(' ').size
            }
            return words
        }
        return null
    }

    private fun getCharCount(file: File, fileType: FileType): Int? {
        if (file.isFile && fileType == FileType.TEXT) {
            return file.length().toInt()
        }
        return null
    }

    // endregion PROPERTIES
}