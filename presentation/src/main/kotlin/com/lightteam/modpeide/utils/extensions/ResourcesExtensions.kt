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

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import java.io.BufferedReader

fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable {
    return ContextCompat.getDrawable(this, drawable) as Drawable
}

fun Context.getScaledDensity(): Float {
    return resources.displayMetrics.scaledDensity
}

fun Context.getRawFileText(@RawRes resId: Int): String {
    val inputStream = resources.openRawResource(resId)
    return inputStream.bufferedReader().use(BufferedReader::readText)
}

fun Context.getTypefaceFromAssets(path: String): Typeface {
    return Typeface.createFromAsset(assets, path)
}