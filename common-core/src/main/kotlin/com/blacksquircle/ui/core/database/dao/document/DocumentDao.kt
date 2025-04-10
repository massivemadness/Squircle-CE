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

package com.blacksquircle.ui.core.database.dao.document

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.blacksquircle.ui.core.database.entity.document.DocumentEntity
import com.blacksquircle.ui.core.database.utils.Tables

@Dao
interface DocumentDao {

    @Query("SELECT * FROM `${Tables.DOCUMENTS}` ORDER BY `position` ASC")
    suspend fun loadAll(): List<DocumentEntity>

    @Query("DELETE FROM `${Tables.DOCUMENTS}`")
    suspend fun deleteAll()

    @Query("DELETE FROM `${Tables.DOCUMENTS}` WHERE `uuid` = :uuid")
    suspend fun deleteWhereEquals(uuid: String)

    @Query("DELETE FROM `${Tables.DOCUMENTS}` WHERE `uuid` != :uuid")
    suspend fun deleteWhereNotEquals(uuid: String)

    @Query("UPDATE `${Tables.DOCUMENTS}` SET `position` = :position WHERE `uuid` = :uuid")
    suspend fun updatePosition(uuid: String, position: Int)

    @Query("UPDATE `${Tables.DOCUMENTS}` SET `position` = `position` - 1 WHERE `position` > :index")
    suspend fun shiftPositions(index: Int)

    @Query(
        "UPDATE `${Tables.DOCUMENTS}` SET " +
            "`modified` = :modified, " +
            "`scroll_x` = :scrollX, " +
            "`scroll_y` = :scrollY, " +
            "`selection_start` = :selectionStart, " +
            "`selection_end` = :selectionEnd " +
            "WHERE `uuid` = :uuid"
    )
    suspend fun updateProperties(
        uuid: String,
        modified: Boolean,
        scrollX: Int,
        scrollY: Int,
        selectionStart: Int,
        selectionEnd: Int
    )

    @Query("UPDATE `${Tables.DOCUMENTS}` SET `modified` = :modified WHERE `uuid` = :uuid")
    suspend fun updateModified(uuid: String, modified: Boolean)

    @Query("UPDATE `${Tables.DOCUMENTS}` SET `language` = :language WHERE `uuid` = :uuid")
    suspend fun updateLanguage(uuid: String, language: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long

    @Transaction
    suspend fun closeDocument(uuid: String, index: Int) {
        deleteWhereEquals(uuid)
        shiftPositions(index)
    }

    @Transaction
    suspend fun closeOtherDocuments(uuid: String) {
        deleteWhereNotEquals(uuid)
        updatePosition(uuid, 0)
    }

    @Transaction
    suspend fun reorderDocuments(fromUuid: String, toUuid: String, fromIndex: Int, toIndex: Int) {
        updatePosition(fromUuid, toIndex)
        updatePosition(toUuid, fromIndex)
    }
}