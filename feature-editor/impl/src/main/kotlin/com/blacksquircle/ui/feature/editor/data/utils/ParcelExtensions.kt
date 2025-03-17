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

package com.blacksquircle.ui.feature.editor.data.utils

import android.os.Parcel
import android.os.Parcelable
import java.io.File

internal fun <T : Parcelable> T.writeFile(file: File) {
    file.outputStream().use { outputStream ->
        val parcel = Parcel.obtain()
        try {
            writeToParcel(parcel, 0)
            val bytes = parcel.marshall()
            outputStream.write(bytes)
        } finally {
            parcel.recycle()
        }
    }
}

internal fun <T> Parcelable.Creator<T>.readFile(file: File): T? {
    file.inputStream().use { inputStream ->
        val bytes = inputStream.readBytes()
        val parcel = Parcel.obtain()
        try {
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            return createFromParcel(parcel)
        } finally {
            parcel.recycle()
        }
    }
}