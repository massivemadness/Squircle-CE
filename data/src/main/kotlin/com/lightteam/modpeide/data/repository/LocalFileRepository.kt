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

package com.lightteam.modpeide.data.repository

import android.os.Environment
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.converter.FileConverter
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.repository.FileRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class LocalFileRepository(
    private val database: AppDatabase,
    private val cacheHandler: CacheHandler
) : FileRepository {

    private val defaultLocation: File = Environment.getExternalStorageDirectory().absoluteFile

    // region EXPLORER

    override fun getDefaultLocation(): FileModel {
        return FileConverter.toModel(defaultLocation)
    }

    override fun makeList(parent: FileModel): Single<List<FileModel>> {
        return Single.create { emitter ->
            val files = getFiles(FileConverter.toFile(parent))
            emitter.onSuccess(files)
        }
    }

    override fun createFile(fileModel: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val realFile: File = FileConverter.toFile(fileModel)
            if(!realFile.exists()) {
                if(fileModel.isFolder) {
                    realFile.mkdirs()
                } else {
                    realFile.createNewFile()
                }
            }
            val modelFile = FileConverter.toModel(realFile)
            emitter.onSuccess(modelFile)
        }
    }

    override fun deleteFile(fileModel: FileModel): Single<FileModel> {
        return Single.create { emitter ->
            val realFile: File = FileConverter.toFile(fileModel)
            if(realFile.exists()) {
                realFile.deleteRecursively()
            }
            val modelFile = FileConverter.toModel(realFile.parentFile)
            emitter.onSuccess(modelFile)
        }
    }

    override fun renameFile(fileModel: FileModel, fileName: String): Single<FileModel> {
        return Single.create { emitter ->
            val originalFile: File = FileConverter.toFile(fileModel)
            val renamedFile = File(originalFile.parentFile.absolutePath + "/$fileName")
            if(originalFile.exists()) {
                originalFile.renameTo(renamedFile)
            }
            val modelFile = FileConverter.toModel(renamedFile.parentFile)
            emitter.onSuccess(modelFile)
        }
    }

    // endregion EXPLORER

    // region EDITOR

    override fun loadAllFiles(): Single<List<DocumentModel>> {
        return database.documentDao().loadDocuments()
            .map { it.map(DocumentConverter::toModel) } // Convert all entities to models
    }

    override fun loadFile(documentModel: DocumentModel): Single<String> {
        return Single.create { emitter ->
            database.documentDao().insert(DocumentConverter.toCache(documentModel)) // Save to Database

            val text = if(cacheHandler.isCached(documentModel)) { // Load from Cache
                cacheHandler.loadFromCache(documentModel)
            } else { // Load from Storage
                val file = File(documentModel.path)
                val builder = StringBuilder()
                file.forEachLine { builder.append(it + '\n') }
                builder.toString()
            }
            cacheHandler.saveToCache(documentModel, text) // Save to Cache
            emitter.onSuccess(text)
        }
    }

    override fun saveFile(documentModel: DocumentModel,
                          //undoStack: UndoStack,
                          //redoStack: UndoStack,
                          text: String): Completable {
        return Completable.create { emitter ->
            database.documentDao().update(DocumentConverter.toCache(documentModel)) // Save to Database

            // Save to Storage
            val file = File(documentModel.path)
            if(file.exists()) {
                val textOutputStreamWriter = OutputStreamWriter(FileOutputStream(file))
                textOutputStreamWriter.write(text)
                textOutputStreamWriter.close()
            }
            cacheHandler.saveToCache(documentModel, text) // Save to Cache
            emitter.onComplete()
        }
    }

    override fun closeFile(documentModel: DocumentModel): Completable {
        return Completable.create { emitter ->
            database.documentDao().delete(DocumentConverter.toCache(documentModel)) // Delete from Database
            cacheHandler.invalidateCache(documentModel) // Delete from Cache
            emitter.onComplete()
        }
    }

    // endregion EDITOR

    private fun getFiles(path: File): MutableList<FileModel> {
        return path.listFiles()
            .map(FileConverter::toModel)
            .toMutableList()
    }
}