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

package com.brackeys.ui.data.database.dao.document

import androidx.room.Dao
import androidx.room.Query
import com.brackeys.ui.data.database.dao.base.BaseDao
import com.brackeys.ui.data.database.entity.document.DocumentEntity
import com.brackeys.ui.data.database.utils.Tables
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class DocumentDao : BaseDao<DocumentEntity> {

    @Query("SELECT * FROM `${Tables.DOCUMENTS}` ORDER BY `position` ASC")
    abstract fun loadAll(): Single<List<DocumentEntity>>

    @Query("DELETE FROM `${Tables.DOCUMENTS}`")
    abstract fun deleteAll(): Completable
}