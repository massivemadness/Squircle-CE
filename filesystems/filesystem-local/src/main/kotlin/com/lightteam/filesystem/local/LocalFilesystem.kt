/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.filesystem.local

import com.github.gzuliyujiang.chardet.CJKCharsetDetector
import com.lightteam.filesystem.base.Filesystem
import com.lightteam.filesystem.base.exception.*
import com.lightteam.filesystem.base.model.*
import com.lightteam.filesystem.base.utils.endsWith
import com.lightteam.filesystem.local.converter.FileConverter
import com.lightteam.filesystem.local.utils.size
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import java.io.IOException
import net.lingala.zip4j.ZipFile

class LocalFilesystem(private val defaultLocation: File) : Filesystem {

    companion object {

        /**
         * zip4j only supports these formats
         */
        private val SUPPORTED_ARCHIVES = arrayOf(".zip", ".jar")
    }

    override fun defaultLocation(): Single<FileTree> {
        return Single.create { emitter ->
            val parent = FileConverter.toModel(defaultLocation)
            if (defaultLocation.isDirectory) {
                val children = defaultLocation.listFiles()!!
                    .map(FileConverter::toModel)
                    .toList()
                val fileTree = FileTree(parent, children)
                emitter.onSuccess(fileTree)
            } else {
                emitter.onError(DirectoryExpectedException())
            }
        }
    }

    override fun provideDirectory(parent: FileModel?): Single<FileTree> {
        return if (parent != null) {
            Single.create { emitter ->
                val file = FileConverter.toFile(parent)
                if (file.isDirectory) {
                    val children = file.listFiles()!!
                        .map(FileConverter::toModel)
                        .toList()
                    val fileTree = FileTree(parent, children)
                    emitter.onSuccess(fileTree)
                } else {
                    emitter.onError(DirectoryExpectedException())
                }
            }
        } else {
            defaultLocation()
        }
    }

    override fun createFile(fileModel: FileModel): Single<FileModel> {
        return Single.create { emitter ->
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
                emitter.onSuccess(fileModel2)
            } else {
                emitter.onError(FileAlreadyExistsException(fileModel.path))
            }
        }
    }

    override fun renameFile(fileModel: FileModel, fileName: String): Single<FileModel> {
        return Single.create { emitter ->
            val originalFile = FileConverter.toFile(fileModel)
            val parentFile = originalFile.parentFile!!
            val renamedFile = File(parentFile, fileName)
            if (originalFile.exists()) {
                if (!renamedFile.exists()) {
                    originalFile.renameTo(renamedFile)
                    val renamedModel = FileConverter.toModel(renamedFile)
                    emitter.onSuccess(renamedModel)
                } else {
                    emitter.onError(FileAlreadyExistsException(renamedFile.absolutePath))
                }
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun deleteFile(fileModel: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val file = FileConverter.toFile(fileModel)
            if (file.exists()) {
                file.deleteRecursively()
                val parentFile = FileConverter.toModel(file.parentFile!!)
                emitter.onSuccess(parentFile)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun copyFile(source: FileModel, dest: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val directory = FileConverter.toFile(dest)
            val sourceFile = FileConverter.toFile(source)
            val destFile = File(directory, sourceFile.name)
            if (sourceFile.exists()) {
                if (!destFile.exists()) {
                    sourceFile.copyRecursively(destFile, overwrite = false)
                    // val destFile2 = FileConverter.toModel(destFile)
                    // emitter.onSuccess(destFile2)
                    emitter.onSuccess(source)
                } else {
                    emitter.onError(FileAlreadyExistsException(dest.path))
                }
            } else {
                emitter.onError(FileNotFoundException(source.path))
            }
        }
    }

    override fun propertiesOf(fileModel: FileModel): Single<PropertiesModel> {
        return Single.create { emitter ->
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
                emitter.onSuccess(result)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun compress(
        source: List<FileModel>,
        dest: FileModel,
        archiveName: String
    ): Observable<FileModel> { // TODO: Use ProgressMonitor
        return Observable.create { emitter ->
            val directory = FileConverter.toFile(dest)
            val archiveFile = ZipFile(File(directory, archiveName))
            if (!archiveFile.file.exists()) {
                for (fileModel in source) {
                    val sourceFile = FileConverter.toFile(fileModel)
                    if (sourceFile.exists()) {
                        if (sourceFile.isDirectory) {
                            archiveFile.addFolder(sourceFile)
                        } else {
                            archiveFile.addFile(sourceFile)
                        }
                        emitter.onNext(fileModel)
                    } else {
                        emitter.onError(FileNotFoundException(fileModel.path))
                    }
                }
            } else {
                emitter.onError(FileAlreadyExistsException(archiveFile.file.absolutePath))
            }
            emitter.onComplete()
        }
    }

    // TODO: Use Observable with ProgressMonitor
    override fun extractAll(source: FileModel, dest: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val sourceFile = FileConverter.toFile(source)
            if (sourceFile.exists()) {
                if (sourceFile.name.endsWith(SUPPORTED_ARCHIVES)) {
                    val archiveFile = ZipFile(sourceFile)
                    when {
                        archiveFile.isValidZipFile -> {
                            archiveFile.extractAll(dest.path)
                            emitter.onSuccess(source)
                        }
                        archiveFile.isEncrypted -> {
                            emitter.onError(EncryptedArchiveException(source.path))
                        }
                        archiveFile.isSplitArchive -> {
                            emitter.onError(SplitArchiveException(source.path))
                        }
                        else -> {
                            emitter.onError(InvalidArchiveException(source.path))
                        }
                    }
                } else {
                    emitter.onError(UnsupportedArchiveException(source.path))
                }
            } else {
                emitter.onError(FileNotFoundException(source.path))
            }
        }
    }

    override fun loadFile(fileModel: FileModel, fileParams: FileParams): Single<String> {
        return Single.create { emitter ->
            val file = File(fileModel.path)
            if (file.exists()) {
                val charset = if (fileParams.chardet) {
                    file.inputStream().use(CJKCharsetDetector::detect)
                } else {
                    fileParams.charset
                }
                try {
                    val text = file.readText(charset = charset)
                    emitter.onSuccess(text)
                } catch (e: OutOfMemoryError) {
                    emitter.onError(OutOfMemoryError(fileModel.path))
                }
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun saveFile(fileModel: FileModel, text: String, fileParams: FileParams): Completable {
        return Completable.create { emitter ->
            try {
                val file = File(fileModel.path)
                if (!file.exists()) {
                    val parentFile = file.parentFile!!
                    if (!parentFile.exists()) {
                        parentFile.mkdirs()
                    }
                    file.createNewFile()
                }
                file.writeText(fileParams.linebreak(text), fileParams.charset)

                emitter.onComplete()
            } catch (e: IOException) {
                emitter.onError(e)
            }
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