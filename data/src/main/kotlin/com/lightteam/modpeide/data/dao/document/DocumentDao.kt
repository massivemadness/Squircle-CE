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

package com.lightteam.modpeide.data.dao.document

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.data.dao.base.BaseDao
import com.lightteam.modpeide.data.entity.DocumentEntity
import com.lightteam.modpeide.data.storage.database.Tables
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class DocumentDao : BaseDao<DocumentEntity> {

    @Query("SELECT * FROM ${Tables.DOCUMENTS}")
    abstract fun loadAll(): Single<List<DocumentEntity>>

    @Query("DELETE FROM ${Tables.DOCUMENTS}")
    abstract fun deleteAll(): Completable
}