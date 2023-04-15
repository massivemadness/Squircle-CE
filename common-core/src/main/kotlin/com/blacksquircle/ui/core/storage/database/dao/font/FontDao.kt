/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.core.storage.database.dao.font

import androidx.room.Dao
import androidx.room.Query
import com.blacksquircle.ui.core.storage.database.dao.base.BaseDao
import com.blacksquircle.ui.core.storage.database.entity.font.FontEntity
import com.blacksquircle.ui.core.storage.database.utils.Tables

@Dao
abstract class FontDao : BaseDao<FontEntity> {

    @Query("SELECT * FROM `${Tables.FONTS}`")
    abstract suspend fun loadAll(): List<FontEntity>

    @Query("SELECT * FROM `${Tables.FONTS}` WHERE `font_name` LIKE '%' || :searchQuery || '%'")
    abstract suspend fun loadAll(searchQuery: String): List<FontEntity>

    @Query("SELECT * FROM `${Tables.FONTS}` WHERE `font_path` = :path")
    abstract suspend fun load(path: String): FontEntity // TODO select by uuid

    @Query("DELETE FROM `${Tables.FONTS}`")
    abstract suspend fun deleteAll()
}