package com.lightteam.modpeide.database.entity.font

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lightteam.modpeide.database.utils.Tables

@Entity(tableName = Tables.FONTS)
data class FontEntity(
    @ColumnInfo(name = "font_name")
    val fontName: String,
    @PrimaryKey
    @ColumnInfo(name = "font_path")
    val fontPath: String,
    @ColumnInfo(name = "support_ligatures")
    val supportLigatures: Boolean,
    @ColumnInfo(name = "is_external")
    val isExternal: Boolean,
    @ColumnInfo(name = "is_paid")
    val isPaid: Boolean
)