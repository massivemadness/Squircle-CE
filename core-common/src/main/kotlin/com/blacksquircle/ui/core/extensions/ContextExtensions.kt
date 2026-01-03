/*
 * Copyright Squircle CE contributors.
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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.getSystemService

fun Context.showToast(
    @StringRes textRes: Int = -1,
    text: CharSequence = "",
    duration: Int = Toast.LENGTH_SHORT,
) {
    if (textRes != -1) {
        Toast.makeText(this, textRes, duration).show()
    } else {
        Toast.makeText(this, text, duration).show()
    }
}

fun Context.copyText(text: String) {
    val clipboardManager = getSystemService<ClipboardManager>()
    val clipData = ClipData.newPlainText("Text", text)
    clipboardManager?.setPrimaryClip(clipData)
}

fun Context.primaryClipText(): String {
    val clipboardManager = getSystemService<ClipboardManager>()
    val clip = clipboardManager?.primaryClip
    if (clip != null && clip.itemCount > 0) {
        return clip.getItemAt(0).text.toString()
    }
    return ""
}

fun View.showSoftInput() {
    val inputMethodManager = context?.getSystemService<InputMethodManager>()
    inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}