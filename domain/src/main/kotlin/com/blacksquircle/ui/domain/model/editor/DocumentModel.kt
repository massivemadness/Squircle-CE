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

package com.blacksquircle.ui.domain.model.editor

data class DocumentModel(
    val uuid: String,
    val path: String,
    var modified: Boolean,
    var position: Int,
    var scrollX: Int,
    var scrollY: Int,
    var selectionStart: Int,
    var selectionEnd: Int
) {

    val name: String
        get() = path.substringAfterLast('/')

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocumentModel
        if (path != other.path) return false
        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + modified.hashCode()
        result = 31 * result + position
        result = 31 * result + scrollX
        result = 31 * result + scrollY
        result = 31 * result + selectionStart
        result = 31 * result + selectionEnd
        return result
    }
}