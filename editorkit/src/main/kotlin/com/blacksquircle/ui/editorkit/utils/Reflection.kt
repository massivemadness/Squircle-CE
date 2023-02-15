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

package com.blacksquircle.ui.editorkit.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import java.lang.reflect.Field

/**
 * https://stackoverflow.com/a/59269370
 */
internal fun TextView.setCursorDrawableColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(color, color)
        )
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 2f, resources.displayMetrics,
        )
        gradient.setSize(width.toInt(), textSize.toInt())
        textCursorDrawable = gradient
        return
    }
    try {
        val editorField = TextView::class.java.findField("mEditor")
        val editor = editorField?.get(this) ?: this
        val editorClass: Class<*> =
            if (editorField != null) editor.javaClass else TextView::class.java
        val cursorRes = TextView::class.java
            .findField("mCursorDrawableRes")?.get(this) as? Int ?: return
        val tintedCursorDrawable = ContextCompat.getDrawable(context, cursorRes)
            ?.tinted(color) ?: return

        val cursorField = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            editorClass.findField("mDrawableForCursor")
        } else {
            null
        }
        if (cursorField != null) {
            cursorField.set(editor, tintedCursorDrawable)
        } else {
            editorClass.findField("mCursorDrawable", "mDrawableForCursor")
                ?.set(editor, arrayOf(tintedCursorDrawable, tintedCursorDrawable))
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

internal fun Class<*>.findField(vararg name: String): Field? {
    name.forEach {
        try {
            val field = getDeclaredField(it)
            field.isAccessible = true
            return field
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    return null
}

internal fun Drawable.tinted(@ColorInt color: Int): Drawable = when (this) {
    is VectorDrawableCompat -> apply { setTintList(ColorStateList.valueOf(color)) }
    is VectorDrawable -> apply { setTintList(ColorStateList.valueOf(color)) }
    else -> DrawableCompat.wrap(this)
        .also { DrawableCompat.setTint(it, color) }
        .let { DrawableCompat.unwrap(it) }
}