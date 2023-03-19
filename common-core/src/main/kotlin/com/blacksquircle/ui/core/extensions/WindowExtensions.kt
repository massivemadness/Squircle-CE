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

import android.app.Activity
import android.view.Window
import android.widget.EditText
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

fun Window.fullscreenMode(whether: Boolean) {
    val controller = WindowCompat.getInsetsController(this, decorView)
    val statusBarType = WindowInsetsCompat.Type.statusBars()
    if (whether) {
        controller.hide(statusBarType)
    } else {
        controller.show(statusBarType)
    }
}

fun Window.decorFitsSystemWindows(decorFitsSystemWindows: Boolean) {
    WindowCompat.setDecorFitsSystemWindows(this, decorFitsSystemWindows)
}

fun Activity.focusedTextField(): EditText? {
    val currentFocusView = currentFocus
    if (currentFocusView is EditText) {
        return currentFocusView
    }
    return null
}