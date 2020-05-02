package com.lightteam.modpeide.ui.settings.adapter

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class PreferenceModel(
    @StringRes
    val title: Int,
    @StringRes
    val subtitle: Int,
    @IdRes
    val navigationId: Int
)