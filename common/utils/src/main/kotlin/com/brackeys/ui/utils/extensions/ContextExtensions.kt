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

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes textRes: Int = -1, text: String = "", duration: Int = Toast.LENGTH_SHORT) {
    if (textRes != -1) {
        Toast.makeText(this, textRes, duration).show()
    } else {
        Toast.makeText(this, text, duration).show()
    }
}

private const val ASSET_PATH = "file:///android_asset/"

fun Context.createTypefaceFromPath(path: String): Typeface {
    return if (path.startsWith(ASSET_PATH)) {
        val newPath = path.substring(22)
        Typeface.createFromAsset(assets, newPath)
    } else {
        try {
            Typeface.createFromFile(path)
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    }
}