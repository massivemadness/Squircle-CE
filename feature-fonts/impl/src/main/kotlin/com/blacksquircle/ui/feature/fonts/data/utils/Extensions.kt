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

package com.blacksquircle.ui.feature.fonts.data.utils

import android.content.Context
import android.graphics.Typeface
import timber.log.Timber

private const val ASSET_PATH = "file:///android_asset/"

internal fun Context.createTypefaceFromPath(fontPath: String): Typeface {
    return try {
        if (fontPath.startsWith(ASSET_PATH)) {
            Typeface.createFromAsset(assets, fontPath.substring(ASSET_PATH.length))
        } else {
            Typeface.createFromFile(fontPath)
        }
    } catch (e: Exception) {
        Timber.e(e, e.message)
        Typeface.MONOSPACE
    }
}