/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.editor.ui.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.InsetDrawable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuCompat
import androidx.core.view.iterator
import com.google.android.material.R

@SuppressLint("RestrictedApi")
class MaterialPopupMenu(private val context: Context) {

    val menu: Menu
        get() = menuBuilder

    private val menuBuilder: MenuBuilder
    private var menuListener: PopupMenu.OnMenuItemClickListener? = null

    init {
        menuBuilder = MenuBuilder(context).apply {
            setCallback(object : MenuBuilder.Callback {
                override fun onMenuModeChange(menu: MenuBuilder) = Unit
                override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                    return menuListener?.onMenuItemClick(item) ?: false
                }
            })
        }
    }

    fun setOnMenuItemClickListener(listener: PopupMenu.OnMenuItemClickListener) {
        this.menuListener = listener
    }

    fun inflate(@MenuRes menuRes: Int) {
        SupportMenuInflater(context).inflate(menuRes, menuBuilder)
        MenuCompat.setGroupDividerEnabled(menuBuilder, true)
        menuBuilder.makeRightPaddingRecursively()
    }

    fun show(anchorView: View) {
        val popupMenu = MenuPopupHelper(
            context, menuBuilder, anchorView, true, R.attr.actionOverflowMenuStyle
        )
        popupMenu.show()
    }
}

/**
 * https://github.com/material-components/material-components-android/commit/560adc655d24f82e3fd866a7840ff7e9db07b301
 */
@SuppressLint("RestrictedApi")
fun Menu.makeRightPaddingRecursively() {
    if (this is MenuBuilder) {
        setOptionalIconsVisible(true)
        for (item in visibleItems) {
            item.makeRightPadding()
            if (item.hasSubMenu()) {
                for (subItem in item.subMenu!!.iterator()) {
                    subItem.makeRightPadding()
                }
            }
        }
    }
}

private fun MenuItem.makeRightPadding() {
    if (icon != null) {
        val iconMarginPx = 8.dpToPx() // 8dp - default margin
        icon = InsetDrawable(icon, iconMarginPx, 0, iconMarginPx, 0)
    }
}

private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()