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

package com.lightteam.localfilesystem.repository

import com.lightteam.filesystem.exception.DirectoryExpectedException
import com.lightteam.filesystem.exception.FileAlreadyExistsException
import com.lightteam.filesystem.exception.FileNotFoundException
import com.lightteam.filesystem.model.*
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.localfilesystem.converter.FileConverter
import com.lightteam.localfilesystem.utils.formatAsDate
import com.lightteam.localfilesystem.utils.formatAsSize
import com.lightteam.localfilesystem.utils.size
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class LocalFilesystem(private val defaultLocation: File) : Filesystem {

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

    override fun copyFile(
        source: FileModel,
        dest: FileModel,
        copyOption: CopyOption
    ): Single<FileModel> {
        return Single.create { emitter ->
            val directory = FileConverter.toFile(dest)
            val sourceFile = FileConverter.toFile(source)
            val destFile = File(directory, sourceFile.name)
            if (sourceFile.exists()) {
                if (!destFile.exists()) {
                    val overwrite = when (copyOption) {
                        CopyOption.ABORT -> false
                        CopyOption.REPLACE -> true
                    }
                    sourceFile.copyRecursively(destFile, overwrite)
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
            if (file.exists()) {
                val result = PropertiesModel(
                    file.name,
                    file.absolutePath,
                    file.lastModified().formatAsDate(),
                    file.size().formatAsSize(),
                    getLineCount(file),
                    getWordCount(file),
                    getCharCount(file),
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
        archiveName: String,
        archiveType: ArchiveType
    ): Observable<FileModel> {
        // TODO support archiveType
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

    override fun decompress(source: FileModel, dest: FileModel): Single<FileModel> {
        TODO("Not yet implemented")
    }

    override fun loadFile(fileModel: FileModel, charset: Charset): Single<String> {
        return Single.create { emitter ->
            val file = File(fileModel.path)
            if (file.exists()) {
                val text = file.readText(charset)
                emitter.onSuccess(text)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun saveFile(fileModel: FileModel, text: String, charset: Charset): Completable {
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
                file.writeText(text, charset)

                emitter.onComplete()
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
    }

    // region PROPERTIES

    private fun getLineCount(file: File): String {
        if (file.isFile) {
            var lines = 0
            file.forEachLine {
                lines++
            }
            return lines.toString()
        }
        return "…"
    }

    private fun getWordCount(file: File): String {
        if (file.isFile) {
            var words = 0
            file.forEachLine {
                words += it.split(' ').size
            }
            return words.toString()
        }
        return "…"
    }

    private fun getCharCount(file: File): String {
        if (file.isFile) {
            return file.length().toString()
        }
        return "…"
    }

    // endregion PROPERTIES
}