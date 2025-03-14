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

package com.blacksquircle.ui.ds.extensions

import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor

fun ComposeColor.toHexString(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    val alpha = this.alpha * 255
    return String.format("#%02x%02x%02x%02x", alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())
}

fun Int.toHexString(fallbackColor: String = "#FFFFFF"): String {
    return try {
        "#" + Integer.toHexString(this)
    } catch (e: Exception) {
        fallbackColor
    }
}

fun Int.isColorDark(threshold: Double = 0.5): Boolean {
    if (this == Color.TRANSPARENT) {
        return false
    }
    val darkness = 1 - (
        0.299 * Color.red(this) +
            0.587 * Color.green(this) +
            0.114 * Color.blue(this)
    ) / 255
    return darkness >= threshold
}