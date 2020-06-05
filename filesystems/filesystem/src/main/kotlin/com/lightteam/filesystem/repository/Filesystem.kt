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

package com.lightteam.filesystem.repository

import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.model.FileTree
import com.lightteam.filesystem.model.ResolveType
import com.lightteam.filesystem.model.PropertiesModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.charset.Charset

interface Filesystem {
    fun defaultLocation(): Single<FileTree>
    fun provideDirectory(parent: FileModel?): Single<FileTree>

    fun createFile(fileModel: FileModel): Single<FileModel>
    fun renameFile(fileModel: FileModel, fileName: String): Single<FileModel>
    fun deleteFile(fileModel: FileModel): Single<FileModel>
    fun copyFile(source: FileModel, dest: FileModel, resolveType: ResolveType): Single<FileModel>
    fun propertiesOf(fileModel: FileModel): Single<PropertiesModel>

    fun loadFile(fileModel: FileModel, charset: Charset): Single<String>
    fun saveFile(fileModel: FileModel, text: String, charset: Charset): Completable
}