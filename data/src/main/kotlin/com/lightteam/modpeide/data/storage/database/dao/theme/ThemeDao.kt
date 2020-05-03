package com.lightteam.modpeide.data.storage.database.dao.theme

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.data.model.entity.ThemeEntity
import com.lightteam.modpeide.data.storage.database.Tables
import com.lightteam.modpeide.data.storage.database.dao.base.BaseDao
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class ThemeDao : BaseDao<ThemeEntity> {

    @Query("SELECT * FROM ${Tables.THEMES}")
    abstract fun loadAll(): Single<List<ThemeEntity>>

    @Query("SELECT * FROM ${Tables.THEMES} WHERE uuid = :uuid")
    abstract fun load(uuid: String): Single<ThemeEntity>

    @Query("DELETE FROM ${Tables.THEMES}")
    abstract fun deleteAll(): Completable
}