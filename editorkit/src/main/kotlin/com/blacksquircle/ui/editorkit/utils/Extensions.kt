/*
 * Copyright 2021 Squircle IDE contributors.
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

import android.content.Context
import android.content.res.Resources

internal val Context.scaledDensity
    get() = resources.displayMetrics.scaledDensity

internal fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
internal fun Int.pxToDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()