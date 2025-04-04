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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat

@Composable
internal fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    val view = LocalView.current
    return remember {
        object : ReorderHapticFeedback {
            override fun perform(type: ReorderHapticFeedbackType) {
                when (type) {
                    ReorderHapticFeedbackType.START ->
                        ViewCompat.performHapticFeedback(
                            view,
                            HapticFeedbackConstantsCompat.GESTURE_START
                        )

                    ReorderHapticFeedbackType.MOVE ->
                        ViewCompat.performHapticFeedback(
                            view,
                            HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
                        )

                    ReorderHapticFeedbackType.END ->
                        ViewCompat.performHapticFeedback(
                            view,
                            HapticFeedbackConstantsCompat.GESTURE_END
                        )
                }
            }
        }
    }
}

internal interface ReorderHapticFeedback {
    fun perform(type: ReorderHapticFeedbackType)
}

internal enum class ReorderHapticFeedbackType {
    START,
    MOVE,
    END,
}