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

package com.blacksquircle.ui.core.database.entity.document

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blacksquircle.ui.core.database.utils.Tables

@Entity(tableName = Tables.DOCUMENTS)
data class DocumentEntity(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "file_uri")
    val fileUri: String,
    @ColumnInfo(name = "filesystem_uuid")
    val filesystemUuid: String,
    @ColumnInfo(name = "language")
    val language: String,
    @ColumnInfo(name = "modified")
    val modified: Boolean,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "scroll_x")
    val scrollX: Int,
    @ColumnInfo(name = "scroll_y")
    val scrollY: Int,
    @ColumnInfo(name = "selection_start")
    val selectionStart: Int,
    @ColumnInfo(name = "selection_end")
    val selectionEnd: Int,
)