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

package com.blacksquircle.ui.core.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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