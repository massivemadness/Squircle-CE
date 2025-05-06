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

package com.blacksquircle.ui.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blacksquircle.ui.core.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.database.dao.font.FontDao
import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.core.database.entity.document.DocumentEntity
import com.blacksquircle.ui.core.database.entity.font.FontEntity
import com.blacksquircle.ui.core.database.entity.server.ServerEntity
import com.blacksquircle.ui.core.database.entity.workspace.WorkspaceEntity

@Database(
    entities = [
        DocumentEntity::class,
        FontEntity::class,
        ServerEntity::class,
        WorkspaceEntity::class,
    ],
    version = 2,
)
abstract class AppDatabaseImpl : RoomDatabase(), AppDatabase {

    companion object {
        const val DATABASE_NAME = "app_database.db"
    }

    abstract override fun documentDao(): DocumentDao
    abstract override fun fontDao(): FontDao
    abstract override fun serverDao(): ServerDao
    abstract override fun workspaceDao(): WorkspaceDao
}