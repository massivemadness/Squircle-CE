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

package com.blacksquircle.ui.core

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.blacksquircle.ui.core.database.AppDatabaseImpl
import com.blacksquircle.ui.core.database.utils.Migrations
import com.blacksquircle.ui.core.database.utils.Tables
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test

class MigrationTest {

    companion object {

        private const val TEST_DB = "app_database_test.db"

        private val ALL_MIGRATIONS = arrayOf(
            Migrations.MIGRATION_1_2,
            Migrations.MIGRATION_2_3,
            Migrations.MIGRATION_3_4,
            Migrations.MIGRATION_4_5,
        )
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabaseImpl::class.java,
    )

    @Test
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabaseImpl::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    fun migrate1To2() {
        // Given
        helper.createDatabase(TEST_DB, 1).apply {
            val values = ContentValues().apply {
                put("file_uri", "file:///storage/emulated/0/Documents/Test.txt")
                put("filesystem_uuid", "local")
            }
            insert("tbl_paths", SQLiteDatabase.CONFLICT_REPLACE, values)
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.MIGRATION_1_2)

        // Then
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='tbl_paths'")
            .use { cursor ->
                assertFalse(cursor.moveToFirst())
            }
        db.query("SELECT * FROM `${Tables.WORKSPACES}`").use { cursor ->
            assertEquals(5, cursor.columnNames.size)
            assertEquals(0, cursor.count)
        }
    }

    @Test
    fun migrate2To3() {
        // Given
        helper.createDatabase(TEST_DB, 2).apply {
            val values = ContentValues().apply {
                put("uuid", "12345")
                put("file_uri", "file:///storage/emulated/0/Documents/Test.txt")
                put("filesystem_uuid", "local")
                put("language", "text.plain")
                put("modified", false)
                put("position", 0)
                put("scroll_x", 0)
                put("scroll_y", 0)
                put("selection_start", 0)
                put("selection_end", 0)
            }
            insert(Tables.DOCUMENTS, SQLiteDatabase.CONFLICT_REPLACE, values)
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, Migrations.MIGRATION_2_3)

        // Then
        db.query("SELECT * FROM `${Tables.DOCUMENTS}`").use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("git_repository")
                do {
                    val columnValue = cursor.getString(columnIndex)
                    assertNull(columnValue)
                } while (cursor.moveToNext())
            }
        }
    }

    @Test
    fun migrate3To4() {
        // Given
        helper.createDatabase(TEST_DB, 3).apply {
            val values = ContentValues().apply {
                put("uuid", "12345")
                put("file_uri", "file:///storage/emulated/0/Documents/Test.txt")
                put("filesystem_uuid", "local")
                put("language", "text.plain")
                put("modified", false)
                put("position", 0)
                put("scroll_x", 0)
                put("scroll_y", 0)
                put("selection_start", 0)
                put("selection_end", 0)
                putNull("git_repository")
            }
            insert(Tables.DOCUMENTS, SQLiteDatabase.CONFLICT_REPLACE, values)
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, Migrations.MIGRATION_3_4)

        // Then
        db.query("SELECT * FROM `${Tables.DOCUMENTS}`").use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("display_name")
                do {
                    val columnValue = cursor.getString(columnIndex)
                    assertEquals("Test.txt", columnValue)
                } while (cursor.moveToNext())
            }
        }
    }

    @Test
    fun migrate4To5() {
        // Given
        helper.createDatabase(TEST_DB, 4).apply {
            val values = ContentValues().apply {
                put("uuid", "12345")
                put("name", "Documents")
                put("type", "local")
                put("file_uri", "file:///storage/emulated/0/Documents")
                put("filesystem_uuid", "local")
            }
            insert(Tables.WORKSPACES, SQLiteDatabase.CONFLICT_REPLACE, values)
            close()
        }

        // When
        val db = helper.runMigrationsAndValidate(TEST_DB, 5, true, Migrations.MIGRATION_4_5)

        // Then
        db.query("SELECT * FROM `${Tables.WORKSPACES}`").use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("type")
                do {
                    val columnValue = cursor.getString(columnIndex)
                    assertEquals("custom", columnValue)
                } while (cursor.moveToNext())
            }
        }
    }
}