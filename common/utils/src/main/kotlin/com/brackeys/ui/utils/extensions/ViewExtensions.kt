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

import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun EditText.debounce(
    coroutineScope: CoroutineScope,
    waitMs: Long = 250L,
    destinationFunction: (String) -> Unit
) {
    var debounceJob: Job? = null
    doAfterTextChanged {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(text.toString())
        }
    }
}

fun SearchView.debounce(
    coroutineScope: CoroutineScope,
    waitMs: Long = 250L,
    destinationFunction: (String) -> Unit
) {
    var debounceJob: Job? = null
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return onQueryTextChange(query)
        }
        override fun onQueryTextChange(newText: String?): Boolean {
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(waitMs)
                destinationFunction(newText ?: "")
            }
            return true
        }
    })
}