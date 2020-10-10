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

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lightteam.modpeide.database.utils.Migrations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    companion object {
        private const val DATABASE_NAME = "database-migration-test"

        private val ALL_MIGRATIONS = arrayOf(
            Migrations.MIGRATION_1_2,
            Migrations.MIGRATION_2_3,
            Migrations.MIGRATION_3_4,
            Migrations.MIGRATION_4_5,
            Migrations.MIGRATION_5_6
        )
    }

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabaseImpl::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrateAll() {
        helper.createDatabase(DATABASE_NAME, 2).apply { // TODO I can't find schema v1
            close()
        }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabaseImpl::class.java,
            DATABASE_NAME
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }
    }
}