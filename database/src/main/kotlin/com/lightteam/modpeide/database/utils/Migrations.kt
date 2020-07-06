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

package com.lightteam.modpeide.database.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DELETE FROM `${Tables.FILE_HISTORY}`")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DELETE FROM `${Tables.FILE_HISTORY}`")
            database.execSQL("ALTER TABLE `${Tables.FILE_HISTORY}` RENAME TO `${Tables.DOCUMENTS}`")
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `${Tables.FONTS}` (
                    `font_name` TEXT NOT NULL, 
                    `font_path` TEXT NOT NULL, 
                    `support_ligatures` INTEGER NOT NULL,
                    `is_external` INTEGER NOT NULL,
                    `is_paid` INTEGER NOT NULL,
                    PRIMARY KEY(`font_path`)
                )
                """
            )
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `${Tables.THEMES}` (
                    `uuid` TEXT NOT NULL, 
                    `name` TEXT NOT NULL, 
                    `author` TEXT NOT NULL, 
                    `description` TEXT NOT NULL, 
                    `is_external` INTEGER NOT NULL, 
                    `is_paid` INTEGER NOT NULL, 
                    `text_color` TEXT NOT NULL, 
                    `background_color` TEXT NOT NULL, 
                    `gutter_color` TEXT NOT NULL, 
                    `gutter_divider_color` TEXT NOT NULL, 
                    `gutter_current_line_number_color` TEXT NOT NULL, 
                    `gutter_text_color` TEXT NOT NULL, 
                    `selected_line_color` TEXT NOT NULL, 
                    `selection_color` TEXT NOT NULL, 
                    `suggestion_query_color` TEXT NOT NULL, 
                    `find_result_background_color` TEXT NOT NULL, 
                    `delimiter_background_color` TEXT NOT NULL, 
                    `number_color` TEXT NOT NULL, 
                    `operator_color` TEXT NOT NULL, 
                    `keyword_color` TEXT NOT NULL, 
                    `type_color` TEXT NOT NULL, 
                    `lang_const_color` TEXT NOT NULL, 
                    `method_color` TEXT NOT NULL, 
                    `string_color` TEXT NOT NULL, 
                    `comment_color` TEXT NOT NULL, 
                    PRIMARY KEY(`uuid`)
                )
                """
            )
        }
    }
}