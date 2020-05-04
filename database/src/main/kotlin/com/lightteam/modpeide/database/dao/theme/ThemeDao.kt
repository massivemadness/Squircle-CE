package com.lightteam.modpeide.database.dao.theme

import androidx.room.Dao
import androidx.room.Query
import com.lightteam.modpeide.database.dao.base.BaseDao
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.database.utils.Tables
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