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

import com.lightteam.modpeide.domain.model.DocumentModel

class DocumentAdapter {

    private val data: ArrayList<DocumentModel> = ArrayList()

    fun add(documentModel: DocumentModel) {
        if(contains(documentModel)) {
            data[indexOf(documentModel)] = documentModel
        } else {
            data.add(documentModel)
        }
    }

    fun get(index: Int): DocumentModel? {
        var document: DocumentModel?
        try {
            document = data[index]
        } catch (e: IndexOutOfBoundsException) {
            document = null
        }
        return document
    }

    fun removeAt(index: Int) = data.removeAt(index)

    fun contains(documentModel: DocumentModel): Boolean {
        data.forEach {
            if(it.path == documentModel.path) {
                return true
            }
        }
        return false
    }

    fun indexOf(documentModel: DocumentModel): Int {
        for(model in data) {
            if(documentModel.path == model.path) {
                return data.indexOf(model)
            }
        }
        return -1
    }

    fun count(): Int = data.size - 1
    fun size(): Int = data.size
    fun isEmpty(): Boolean = data.isEmpty()
}