/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.database.delegate

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brackeys.ui.database.AppDatabase
import com.brackeys.ui.database.AppDatabaseImpl
import com.brackeys.ui.database.utils.Tables

object DatabaseDelegate {

    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabaseImpl::class.java, AppDatabaseImpl.DATABASE_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("""
                        INSERT OR IGNORE INTO `${Tables.FONTS}` VALUES
                            ('Droid Sans Mono','file:///android_asset/fonts/droid_sans_mono.ttf',0,0),
                            ('JetBrains Mono','file:///android_asset/fonts/jetbrains_mono.ttf',1,0),
                            ('Fira Code','file:///android_asset/fonts/fira_code.ttf',1,0),
                            ('Source Code Pro','file:///android_asset/fonts/source_code_pro.ttf',0,0),
                            ('Anonymous Pro','file:///android_asset/fonts/anonymous_pro.ttf',0,0),
                            ('DejaVu Sans Mono','file:///android_asset/fonts/dejavu_sans_mono.ttf',0,0);
                            """
                    )
                }
            })
            .build()
    }
}