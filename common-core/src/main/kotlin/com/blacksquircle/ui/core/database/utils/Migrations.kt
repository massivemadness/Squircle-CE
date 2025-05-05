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

package com.blacksquircle.ui.core.database.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS `tbl_paths`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `${Tables.WORKSPACES}` (`uuid` TEXT NOT NULL, " +
                    "`name` TEXT NOT NULL, `type` TEXT NOT NULL, `file_uri` TEXT NOT NULL, " +
                    "`filesystem_uuid` TEXT NOT NULL, PRIMARY KEY(`uuid`))"
            )
        }
    }
}