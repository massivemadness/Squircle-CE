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

import android.widget.TextView

val TextView.topVisibleLine: Int
    get() {
        if (layout == null || lineHeight == 0) {
            return 0
        }
        val line = layout.getLineForVertical(scrollY)
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else {
            line
        }
    }

val TextView.bottomVisibleLine: Int
    get() {
        if (layout == null || lineHeight == 0) {
            return 0
        }
        val line = layout.getLineForVertical(scrollY + height)
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else {
            line
        }
    }