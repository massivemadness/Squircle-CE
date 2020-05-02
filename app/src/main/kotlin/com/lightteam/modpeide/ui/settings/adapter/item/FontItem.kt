package com.lightteam.modpeide.ui.settings.adapter.item

data class FontItem(
    val fontName: String,
    val fontPath: String,
    val supportLigatures: Boolean,
    /*val isExternal: Boolean,*/
    val isPaid: Boolean
)