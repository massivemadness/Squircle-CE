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

package com.lightteam.modpeide.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lightteam.modpeide.database.dao.document.DocumentDao
import com.lightteam.modpeide.database.entity.document.DocumentEntity
import com.lightteam.modpeide.database.entity.font.FontEntity
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.database.dao.font.FontDao
import com.lightteam.modpeide.database.dao.theme.ThemeDao

@Database(entities = [
    DocumentEntity::class,
    FontEntity::class,
    ThemeEntity::class
], version = 3)
abstract class AppDatabaseImpl : RoomDatabase(), AppDatabase {

    companion object {
        const val DATABASE_NAME = "database"
    }

    abstract override fun documentDao(): DocumentDao
    abstract override fun fontDao(): FontDao
    abstract override fun themeDao(): ThemeDao

    override fun shutDown() {
        clearAllTables()
    }
}