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

package com.blacksquircle.ui.feature.fonts.data.model

internal enum class AssetsFont(
    val fontId: String,
    val fontName: String,
    val fontUri: String,
) {
    JETBRAINS_MONO(
        fontId = "jetbrains_mono",
        fontName = "JetBrains Mono",
        fontUri = "file:///android_asset/fonts/jetbrains_mono.ttf",
    ),
    DROID_SANS_MONO(
        fontId = "droid_sans_mono",
        fontName = "Droid Sans Mono",
        fontUri = "file:///android_asset/fonts/droid_sans_mono.ttf",
    ),
    FIRA_CODE(
        fontId = "fira_code",
        fontName = "Fira Code",
        fontUri = "file:///android_asset/fonts/fira_code.ttf",
    ),
    SOURCE_CODE_PRO(
        fontId = "source_code_pro",
        fontName = "Source Code Pro",
        fontUri = "file:///android_asset/fonts/source_code_pro.ttf",
    ),
    ANONYMOUS_PRO(
        fontId = "anonymous_pro",
        fontName = "Anonymous Pro",
        fontUri = "file:///android_asset/fonts/anonymous_pro.ttf",
    ),
    DEJAVU_SANS_MONO(
        fontId = "dejavu_sans_mono",
        fontName = "DejaVu Sans Mono",
        fontUri = "file:///android_asset/fonts/dejavu_sans_mono.ttf",
    );

    companion object {

        fun find(uuid: String): AssetsFont? {
            return entries.find { it.fontId == uuid }
        }
    }
}