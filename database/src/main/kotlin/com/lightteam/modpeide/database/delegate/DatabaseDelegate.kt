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

package com.lightteam.modpeide.database.delegate

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.database.AppDatabaseImpl
import com.lightteam.modpeide.database.utils.Migrations
import com.lightteam.modpeide.database.utils.Tables

object DatabaseDelegate {

    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabaseImpl::class.java, AppDatabaseImpl.DATABASE_NAME)
            .addMigrations(
                Migrations.MIGRATION_1_2,
                Migrations.MIGRATION_2_3,
                Migrations.MIGRATION_3_4
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("""
                        INSERT OR IGNORE INTO `${Tables.FONTS}` VALUES
                            ('Droid Sans Mono','file:///android_asset/fonts/droid_sans_mono.ttf',0,0,0),
                            ('JetBrains Mono','file:///android_asset/fonts/jetbrains_mono.ttf',1,0,0),
                            ('Fira Code','file:///android_asset/fonts/fira_code.ttf',1,0,1),
                            ('Source Code Pro','file:///android_asset/fonts/source_code_pro.ttf',0,0,1),
                            ('Anonymous Pro','file:///android_asset/fonts/anonymous_pro.ttf',0,0,1),
                            ('DejaVu Sans Mono','file:///android_asset/fonts/dejavu_sans_mono.ttf',0,0,1);
                            """
                    )
                    db.execSQL("""
                        INSERT OR IGNORE INTO `${Tables.THEMES}` VALUES
                            ('964c249d-ad3c-4d85-8010-f3d55c1ae0a2','Darcula','Light Team Software','Default color scheme',0,0,'#ABB7C5','#303030','#313335','#555555','#A4A3A3','#616366','#3A3A3A','#28427F','#987DAC','#33654B','#33654B','#6897BB','#E8E2B7','#EC7600','#EC7600','#EC7600','#FEC76C','#6E875A','#66747B'),
                            ('3f2f13b0-475f-4b4b-9da3-e4f5e2b5959f','Monokai','Light Team Software','Default color scheme',0,1,'#F8F8F8','#272823','#272823','#5B5A4F','#C8BBAC','#5B5A4F','#34352D','#666666','#7CE0F3','#5F5E5A','#5F5E5A','#BB8FF8','#F8F8F2','#EB347E','#7FD0E4','#EB347E','#B6E951','#EBE48C','#89826D'),
                            ('069a454f-d0ee-44ed-959e-6416ad304358','Obsidian','Light Team Software','Default color scheme',0,1,'#E0E2E4','#2A3134','#2A3134','#67777B','#E0E0E0','#859599','#31393C','#616161','#9EC56F','#838177','#616161','#F8CE4E','#E7E2BC','#9EC56F','#9EC56F','#9EC56F','#E7E2BC','#DE7C2E','#808C92'),
                            ('a484d6ae-9410-4798-9a34-d27538d6255d','Ladies Night','Light Team Software','Default color scheme',0,1,'#E0E2E4','#22282C','#2A3134','#4F575A','#E0E2E4','#859599','#373340','#5B2B41','#6E8BAE','#8A4364','#616161','#7EFBFD','#E7E2BC','#DA89A2','#DA89A2','#DA89A2','#8FB4C5','#75D367','#808C92'),
                            ('8a627f6f-59a8-40f9-8ad3-1ab32150cabe','Tomorrow Night','Light Team Software','Default color scheme',0,1,'#C6C8C6','#222426','#222426','#4B4D51','#FFFFFF','#C6C8C6','#2D2F33','#383B40','#EAC780','#4B4E54','#616161','#D49668','#CFD1CF','#AD95B8','#AD95B8','#AD95B8','#87A1BB','#B7BC73','#969896'),
                            ('f3ba9e8a-b594-4697-b0d1-526c2465f8d9','Visual Studio 2013','Light Team Software','Default color scheme',0,1,'#C8C8C8','#232323','#2C2C2C','#555555','#FFFFFF','#C6C8C6','#141414','#454464','#4F98F7','#1C3D6B','#616161','#BACDAB','#DCDCDC','#669BD1','#669BD1','#669BD1','#71C6B1','#CE9F89','#6BA455');
                            """
                    )
                    db.execSQL("""
                        INSERT OR IGNORE INTO `${Tables.PRESETS}` VALUES
                            ('59d24164-3e1f-4a6d-bb8d-0ee23b4083e6', 'Default', 1, '{}();,.=\|&![]<>+-/*?:_')
                            """
                    )
                }
            })
            .build()
    }
}