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
import java.io.File

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

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `${Tables.DOCUMENTS}` ADD COLUMN `git_repository` TEXT")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `${Tables.DOCUMENTS}` ADD COLUMN `display_name` TEXT NOT NULL DEFAULT ''")

            val updates = mutableListOf<Pair<String, String>>()

            db.query("SELECT uuid, file_uri FROM `${Tables.DOCUMENTS}`").use { cursor ->
                val idIndex = cursor.getColumnIndex("uuid")
                val uriIndex = cursor.getColumnIndex("file_uri")
                while (cursor.moveToNext()) {
                    val uuid = cursor.getString(idIndex)
                    val fileUri = cursor.getString(uriIndex)
                    val displayName = fileUri.substringAfterLast(File.separatorChar)
                    updates.add(uuid to displayName)
                }
            }

            val statement = db.compileStatement("UPDATE `${Tables.DOCUMENTS}` SET `display_name` = ? WHERE uuid = ?")
            for ((uuid, displayName) in updates) {
                statement.bindString(1, displayName)
                statement.bindString(2, uuid)
                statement.execute()
                statement.clearBindings()
            }
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("UPDATE `${Tables.WORKSPACES}` SET `type` = 'custom'")
        }
    }
}