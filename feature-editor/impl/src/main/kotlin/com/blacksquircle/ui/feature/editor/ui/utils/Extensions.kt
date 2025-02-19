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

package com.blacksquircle.ui.feature.editor.ui.utils

import android.app.Activity
import android.net.Uri
import android.widget.EditText

fun <T> MutableList<T>.replaceList(collection: Collection<T>): List<T> {
    val temp = collection.toList()
    clear()
    addAll(temp)
    return this
}

fun <T> MutableList<T>.appendList(element: T): List<T> {
    if (!contains(element)) {
        add(element)
    }
    return this
}

fun String.encodeUri(): String = Uri.encode(this)
fun String.decodeUri(): String = Uri.decode(this)

fun Activity.focusedTextField(): EditText? {
    val currentFocusView = currentFocus
    if (currentFocusView is EditText) {
        return currentFocusView
    }
    return null
}