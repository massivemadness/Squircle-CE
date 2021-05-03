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

package com.brackeys.ui.feature.explorer.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.brackeys.ui.feature.explorer.R
import com.brackeys.ui.utils.extensions.getColour
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    val parentActivity = activity as? AppCompatActivity
    parentActivity?.setSupportActionBar(toolbar)
}

val Fragment.supportActionBar: ActionBar?
    get() = (activity as? AppCompatActivity)?.supportActionBar

fun NavController.popBackStack(n: Int) {
    for (index in 0 until n) {
        popBackStack()
    }
}

fun Toolbar.replaceMenu(@MenuRes menuRes: Int) {
    menu.clear()
    inflateMenu(menuRes)
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    imageTintList = ColorStateList.valueOf(
        context.getColour(colorRes)
    )
}

fun View.setSelectableBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun <T> MutableList<T>.replaceList(collection: Collection<T>) {
    clear()
    addAll(collection)
}

fun Long.toReadableDate(pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.toReadableSize(): String {
    if (this <= 0)
        return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return (DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())) +
        " " + units[digitGroups])
}

fun String.clipText(context: Context?) = clip(context, ClipData.newPlainText("Text", this))

private fun clip(context: Context?, data: ClipData) {
    context?.getSystemService<ClipboardManager>()?.setPrimaryClip(data)
}