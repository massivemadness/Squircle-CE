package com.lightteam.modpeide.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lightteam.modpeide.data.storage.database.Tables

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