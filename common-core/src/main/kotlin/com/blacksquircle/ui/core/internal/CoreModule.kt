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

package com.blacksquircle.ui.core.internal

import android.content.Context
import androidx.room.Room
import com.blacksquircle.ui.core.database.AppDatabase
import com.blacksquircle.ui.core.database.AppDatabaseImpl
import com.blacksquircle.ui.core.database.utils.Migrations
import com.blacksquircle.ui.core.settings.SettingsManager
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
object CoreModule {

    @Provides
    @Singleton
    fun provideSettingsManager(context: Context): SettingsManager {
        return SettingsManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabaseImpl::class.java, AppDatabaseImpl.DATABASE_NAME)
            .addMigrations(
                Migrations.MIGRATION_1_2,
                Migrations.MIGRATION_2_3,
                Migrations.MIGRATION_3_4,
                Migrations.MIGRATION_4_5,
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideJsonParser(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .baseUrl("http://blacksquircle.com")
            .build()
    }
}