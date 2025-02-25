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

package com.blacksquircle.ui.core.storage.database.dao.theme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blacksquircle.ui.core.storage.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.core.storage.database.utils.Tables

@Dao
interface ThemeDao {

    @Query("SELECT * FROM `${Tables.THEMES}`")
    suspend fun loadAll(): List<ThemeEntity>

    @Query("DELETE FROM `${Tables.THEMES}`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `${Tables.THEMES}` WHERE `uuid` = :uuid")
    suspend fun load(uuid: String): ThemeEntity

    @Query("DELETE FROM `${Tables.THEMES}` WHERE `uuid` = :uuid")
    suspend fun delete(uuid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(themeEntity: ThemeEntity): Long
}