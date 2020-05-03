package com.lightteam.modpeide.data.feature.font

data class FontModel(
    val fontName: String,
    val fontPath: String,
    val supportLigatures: Boolean,
    val isExternal: Boolean,
    val isPaid: Boolean
)