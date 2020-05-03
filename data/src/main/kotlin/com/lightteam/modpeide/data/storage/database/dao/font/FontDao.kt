package com.lightteam.modpeide.data.storage.database.dao.font

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.data.model.entity.FontEntity
import com.lightteam.modpeide.data.storage.database.Tables
import com.lightteam.modpeide.data.storage.database.dao.base.BaseDao
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class FontDao : BaseDao<FontEntity> {

    @Query("SELECT * FROM ${Tables.FONTS}")
    abstract fun loadAll(): Single<List<FontEntity>>

    @Query("DELETE FROM ${Tables.FONTS}")
    abstract fun deleteAll(): Completable
}