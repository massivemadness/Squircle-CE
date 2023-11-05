/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.core.extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.InsetDrawable
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.R
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.iterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun View.applySystemWindowInsets(
    consume: Boolean,
    block: (Int, Int, Int, Int) -> Unit,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val statusBarType = WindowInsetsCompat.Type.statusBars()
        val navigationBarType = WindowInsetsCompat.Type.navigationBars()
        val imeType = WindowInsetsCompat.Type.ime()
        val systemWindowInsets = insets.getInsets(statusBarType or navigationBarType or imeType)

        block(
            systemWindowInsets.left,
            systemWindowInsets.top,
            systemWindowInsets.right,
            systemWindowInsets.bottom,
        )

        if (consume) {
            WindowInsetsCompat.CONSUMED
        } else {
            insets
        }
    }
}

fun View.setSelectableBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun View.setSelectedBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(R.attr.colorControlHighlight, this, true)
    setBackgroundResource(resourceId)
}

fun View.setActivatedBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.activatedBackgroundIndicator, this, true)
    setBackgroundResource(resourceId)
}

fun SearchView.debounce(
    coroutineScope: CoroutineScope,
    waitMs: Long = 250L,
    destinationFunction: (String) -> Unit,
) {
    var debounceJob: Job? = null
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return onQueryTextChange(query)
        }
        override fun onQueryTextChange(newText: String?): Boolean {
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(waitMs)
                destinationFunction(newText.orEmpty())
            }
            return true
        }
    })
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    imageTintList = ColorStateList.valueOf(
        context.getColour(colorRes),
    )
}

fun ImageView.setTintAttr(@AttrRes attrRes: Int) {
    imageTintList = ColorStateList.valueOf(
        context.getColorAttr(attrRes),
    )
}

fun AppCompatSpinner.dismiss() {
    val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
    popup.isAccessible = true
    val listPopupWindow = popup.get(this) as ListPopupWindow
    listPopupWindow.dismiss()
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