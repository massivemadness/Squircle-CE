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

package com.blacksquircle.ui.core.storage.database.dao.server

import androidx.room.Dao
import androidx.room.Query
import com.blacksquircle.ui.core.storage.database.dao.base.BaseDao
import com.blacksquircle.ui.core.storage.database.entity.server.ServerEntity
import com.blacksquircle.ui.core.storage.database.utils.Tables
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ServerDao : BaseDao<ServerEntity> {

    @Query("SELECT * FROM `${Tables.SERVERS}`")
    abstract fun flow(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM `${Tables.SERVERS}`")
    abstract suspend fun loadAll(): List<ServerEntity>

    @Query("SELECT * FROM `${Tables.SERVERS}` WHERE `uuid` = :uuid")
    abstract suspend fun load(uuid: String): ServerEntity

    @Query("DELETE FROM `${Tables.SERVERS}`")
    abstract suspend fun deleteAll()
}