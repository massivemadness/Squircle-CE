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
import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.model.FileTree
import com.lightteam.filesystem.model.PropertiesModel
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.localfilesystem.converter.FileConverter
import com.lightteam.localfilesystem.utils.formatAsDate
import com.lightteam.localfilesystem.utils.formatAsSize
import com.lightteam.localfilesystem.utils.size
import io.reactivex.Completable
import io.reactivex.Single
import java.io.BufferedReader
import java.io.File
import java.io.IOException

class LocalFilesystem(private val defaultLocation: File) : Filesystem {

    // region EXPLORER

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
                val realFile = FileConverter.toFile(parent)
                if (realFile.isDirectory) {
                    val children = realFile.listFiles()!!
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
            val realFile = FileConverter.toFile(fileModel)
            if (!realFile.exists()) {
                if (fileModel.isFolder) {
                    realFile.mkdirs()
                } else {
                    realFile.createNewFile()
                }
                val modelFile = FileConverter.toModel(realFile)
                emitter.onSuccess(modelFile)
            } else {
                emitter.onError(FileAlreadyExistsException())
            }
        }
    }

    override fun deleteFile(fileModel: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val realFile = FileConverter.toFile(fileModel)
            if (realFile.exists()) {
                realFile.deleteRecursively()
                val parentFile = FileConverter.toModel(realFile.parentFile!!)
                emitter.onSuccess(parentFile)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun renameFile(fileModel: FileModel, fileName: String): Single<FileModel> {
        return Single.create { emitter ->
            val originalFile = FileConverter.toFile(fileModel)
            val parentFile = File(originalFile.parentFile!!.absolutePath)
            val renamedFile = File(parentFile, fileName)
            if (originalFile.exists()) {
                if (!renamedFile.exists()) {
                    originalFile.renameTo(renamedFile)
                    val parentModel = FileConverter.toModel(parentFile)
                    emitter.onSuccess(parentModel)
                } else {
                    emitter.onError(FileAlreadyExistsException())
                }
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun propertiesOf(fileModel: FileModel): Single<PropertiesModel> {
        return Single.create { emitter ->
            val realFile = File(fileModel.path)
            if (realFile.exists()) {
                val result = PropertiesModel(
                    fileModel.name,
                    fileModel.path,
                    fileModel.lastModified.formatAsDate(),
                    realFile.size().formatAsSize(),
                    getLineCount(realFile),
                    getWordCount(realFile),
                    getCharCount(realFile),
                    realFile.canRead(),
                    realFile.canWrite(),
                    realFile.canExecute()
                )
                emitter.onSuccess(result)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    // endregion EXPLORER

    // region EDITOR

    override fun loadFile(fileModel: FileModel): Single<String> {
        return Single.create { emitter ->
            val file = File(fileModel.path)
            if (file.exists()) {
                val text = file.inputStream().bufferedReader().use(BufferedReader::readText)
                emitter.onSuccess(text)
            } else {
                emitter.onError(FileNotFoundException(fileModel.path))
            }
        }
    }

    override fun saveFile(fileModel: FileModel, text: String): Completable {
        return Completable.create { emitter ->
            try {
                val file = File(fileModel.path)
                if (!file.exists()) {
                    file.createNewFile()
                }

                val writer = file.outputStream().bufferedWriter()
                writer.write(text)
                writer.close()

                emitter.onComplete()
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
    }

    // endregion EDITOR

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