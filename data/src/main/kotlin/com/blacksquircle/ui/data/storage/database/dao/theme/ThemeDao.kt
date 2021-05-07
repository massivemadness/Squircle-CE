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

package com.blacksquircle.ui.data.storage.database.dao.theme

import androidx.room.Dao
import androidx.room.Query
import com.blacksquircle.ui.data.storage.database.dao.base.BaseDao
import com.blacksquircle.ui.data.storage.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.data.storage.database.utils.Tables

@Dao
abstract class ThemeDao : BaseDao<ThemeEntity> {

    @Query("SELECT * FROM `${Tables.THEMES}` WHERE `name` LIKE '%' || :searchQuery || '%'")
    abstract suspend fun loadAll(searchQuery: String): List<ThemeEntity>

    @Query("SELECT * FROM `${Tables.THEMES}` WHERE `uuid` = :uuid")
    abstract suspend fun load(uuid: String): ThemeEntity

    @Query("DELETE FROM `${Tables.THEMES}`")
    abstract suspend fun deleteAll()
}