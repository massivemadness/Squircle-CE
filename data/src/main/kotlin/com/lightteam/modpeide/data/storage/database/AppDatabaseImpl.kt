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

package com.lightteam.modpeide.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lightteam.modpeide.data.dao.document.DocumentDao
import com.lightteam.modpeide.data.model.entity.DocumentEntity

@Database(entities = [
    DocumentEntity::class
], version = 2)
abstract class AppDatabaseImpl : RoomDatabase(), AppDatabase {

    companion object {
        const val DATABASE_NAME = "database"
    }

    abstract override fun documentDao(): DocumentDao

    override fun shutDown() {
        clearAllTables()
    }
}