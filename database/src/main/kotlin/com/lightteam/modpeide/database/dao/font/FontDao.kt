package com.lightteam.modpeide.database.dao.font

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.database.dao.base.BaseDao
import com.lightteam.modpeide.database.entity.font.FontEntity
import com.lightteam.modpeide.database.utils.Tables
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class FontDao : BaseDao<FontEntity> {

    @Query("SELECT * FROM ${Tables.FONTS}")
    abstract fun loadAll(): Single<List<FontEntity>>

    @Query("DELETE FROM ${Tables.FONTS}")
    abstract fun deleteAll(): Completable
}