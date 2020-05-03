package com.lightteam.modpeide.data.feature.scheme

data class Theme(
    val uuid: String,
    val name: String,
    val author: String,
    val description: String,
    val isExternal: Boolean,
    val isPaid: Boolean,
    val colorScheme: ColorScheme
)