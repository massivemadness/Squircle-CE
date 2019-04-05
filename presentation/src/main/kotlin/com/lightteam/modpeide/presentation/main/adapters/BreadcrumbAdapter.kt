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

package com.lightteam.modpeide.presentation.main.adapters

import com.lightteam.modpeide.domain.model.FileModel

class BreadcrumbAdapter {

    private val data: MutableList<FileModel> = mutableListOf()

    fun add(fileModel: FileModel) = data.add(fileModel)
    fun get(index: Int): FileModel = data[index]
    fun remove(index: Int) = data.removeAt(index)
    fun contains(fileModel: FileModel): Boolean = data.contains(fileModel)
    fun indexOf(fileModel: FileModel): Int = data.indexOf(fileModel)
    fun getCount(): Int = data.size
}