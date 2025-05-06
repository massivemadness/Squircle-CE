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

package com.blacksquircle.ui.filesystem.root

import com.blacksquircle.ui.filesystem.base.Filesystem
import com.blacksquircle.ui.filesystem.base.exception.DirectoryExpectedException
import com.blacksquircle.ui.filesystem.base.exception.FileAlreadyExistsException
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileParams
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.blacksquircle.ui.filesystem.root.utils.requestRootAccess
import com.ibm.icu.text.CharsetDetector
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.flow.Flow
import java.io.BufferedReader
import java.io.File

class RootFilesystem : Filesystem {

    init {
        requestRootAccess()
    }

    override fun ping() = Unit

    override fun listFiles(parent: FileModel): List<FileModel> {
        val file = toFileObject(parent)
        if (!file.isDirectory) {
            throw DirectoryExpectedException()
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
            throw FileNotFoundException(source.path)
        }
        if (destFile.exists()) {
            throw FileAlreadyExistsException(destFile.absolutePath)
        }
        sourceFile.renameTo(destFile)
    }

    override fun deleteFile(fileModel: FileModel) {
        val file = toFileObject(fileModel)
        if (!file.exists()) {
            throw FileNotFoundException(fileModel.path)
        }
        file.deleteRecursive()
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
        val file = toFileObject(fileModel)
        if (!file.exists()) {
            throw FileNotFoundException(fileModel.path)
        }
        val charset = if (fileParams.chardet) {
            try {
                val charsetMatch = CharsetDetector()
                    .setText(file.newInputStream().readBytes())
                    .detect()
                charset(charsetMatch.name)
            } catch (e: Exception) {
                Charsets.UTF_8
            }
        } else {
            fileParams.charset
        }
        return file.newInputStream()
            .bufferedReader(charset)
            .use(BufferedReader::readText)
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams) {
        val file = toFileObject(fileModel)
        if (!file.exists()) {
            val parentFile = file.parentFile!!
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }
        file.newOutputStream().bufferedWriter(fileParams.charset).use { writer ->
            val output = text.replace(
                regex = fileParams.linebreak.regex,
                replacement = fileParams.linebreak.replacement
            )
            writer.write(output)
        }
    }

    companion object : Filesystem.Mapper<SuFile> {

        const val ROOT_UUID = "root"
        const val ROOT_SCHEME = "sufile://"

        init {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR),
            )
        }

        @Suppress("KotlinConstantConditions")
        override fun toFileModel(fileObject: SuFile): FileModel {
            return FileModel(
                fileUri = ROOT_SCHEME + fileObject.path,
                filesystemUuid = ROOT_UUID,
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

        override fun toFileObject(fileModel: FileModel): SuFile {
            return SuFile(fileModel.path)
        }
    }
}