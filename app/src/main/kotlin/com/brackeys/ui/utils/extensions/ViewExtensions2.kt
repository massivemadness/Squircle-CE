/*
 * Copyright 2021 Brackeys IDE contributors.
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

package com.brackeys.ui.utils.extensions

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    val parentActivity = activity as AppCompatActivity
    parentActivity.setSupportActionBar(toolbar)
}

val Fragment.supportActionBar: ActionBar?
    get() = (activity as? AppCompatActivity)?.supportActionBar

fun NavController.popBackStack(n: Int) {
    for (index in 0 until n) {
        popBackStack()
    }
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    imageTintList = ColorStateList.valueOf(
        context.getColour(colorRes)
    )
}

fun View.setSelectableBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun Toolbar.replaceMenu(@MenuRes menuRes: Int) {
    menu.clear()
    inflateMenu(menuRes)
}

/**
 * https://stackoverflow.com/a/17802569/4405457
 */
fun DrawerLayout.multiplyDraggingEdgeSizeBy(n: Int) {
    val leftDragger = javaClass.getDeclaredField("mLeftDragger")
    leftDragger.isAccessible = true

    val viewDragHelper = leftDragger.get(this) as ViewDragHelper
    val edgeSize = viewDragHelper.javaClass.getDeclaredField("mEdgeSize")
    edgeSize.isAccessible = true

    val edge = edgeSize.getInt(viewDragHelper)
    edgeSize.setInt(viewDragHelper, edge * n)
}