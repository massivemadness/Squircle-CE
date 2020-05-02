package com.lightteam.modpeide.ui.settings.adapter.item

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class PreferenceItem(
    @StringRes
    val title: Int,
    @StringRes
    val subtitle: Int,
    @IdRes
    val navigationId: Int
)