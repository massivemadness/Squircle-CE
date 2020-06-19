/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.utils.extensions

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.lightteam.modpeide.BuildConfig
import java.io.BufferedReader

private const val FLAVOR_STANDARD = "standard"
private const val FLAVOR_ULTIMATE = "ultimate"

fun isUltimate(): Boolean {
    return when (BuildConfig.FLAVOR) {
        FLAVOR_STANDARD -> false
        FLAVOR_ULTIMATE -> true
        else -> false
    }
}

fun Context.getColour(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.getRawFileText(@RawRes resId: Int): String {
    val inputStream = resources.openRawResource(resId)
    return inputStream.bufferedReader().use(BufferedReader::readText)
}

fun Context.hasExternalStorageAccess(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

private const val ASSET_PATH = "file:///android_asset/"

fun Context.createTypefaceFromPath(path: String): Typeface {
    return if (path.startsWith(ASSET_PATH)) {
        val newPath = path.substring(22)
        Typeface.createFromAsset(assets, newPath)
    } else {
        try {
            Typeface.createFromFile(path)
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    }
}

fun String.clipText(context: Context?) = clip(context, ClipData.newPlainText("Text", this))

private fun clip(context: Context?, data: ClipData) {
    context?.getSystemService<ClipboardManager>()?.setPrimaryClip(data)
}