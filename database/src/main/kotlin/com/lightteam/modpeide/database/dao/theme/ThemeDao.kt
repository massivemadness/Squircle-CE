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

package com.lightteam.modpeide.database.dao.theme

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.database.dao.base.BaseDao
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.database.utils.Tables
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class ThemeDao : BaseDao<ThemeEntity> {

    @Query("SELECT * FROM ${Tables.THEMES} WHERE name LIKE '%' || :searchQuery || '%'")
    abstract fun loadAll(searchQuery: String): Single<List<ThemeEntity>>

    @Query("SELECT * FROM ${Tables.THEMES} WHERE uuid = :uuid")
    abstract fun load(uuid: String): Single<ThemeEntity>

    @Query("DELETE FROM ${Tables.THEMES}")
    abstract fun deleteAll(): Completable
}