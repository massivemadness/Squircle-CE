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

package com.blacksquircle.ui.ds.modifier

import android.os.SystemClock

internal const val DefaultMs = 250L

internal fun debounceLambda(action: (() -> Unit), debounceMs: Long): (() -> Unit) {
    return {
        if (DebounceState.isClickAllowed(debounceMs)) {
            DebounceState.onClickInvoked()
            action.invoke()
        }
    }
}

private object DebounceState {

    private var lastClickTimestamp = 0L

    fun isClickAllowed(debounceMs: Long): Boolean {
        val delta = currentTimestamp() - lastClickTimestamp
        return delta !in 0L..debounceMs
    }

    fun onClickInvoked() {
        lastClickTimestamp = currentTimestamp()
    }

    private fun currentTimestamp(): Long {
        return SystemClock.elapsedRealtime()
    }
}