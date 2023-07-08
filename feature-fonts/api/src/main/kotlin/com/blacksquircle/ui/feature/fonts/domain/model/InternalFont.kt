/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.fonts.domain.model

enum class InternalFont(val font: FontModel) {
    DROID_SANS_MONO(
        font = FontModel(
            fontUuid = "droid_sans_mono",
            fontName = "Droid Sans Mono",
            fontPath = "file:///android_asset/fonts/droid_sans_mono.ttf",
            isExternal = false,
        )
    ),
    JETBRAINS_MONO(
        font = FontModel(
            fontUuid = "jetbrains_mono",
            fontName = "JetBrains Mono",
            fontPath = "file:///android_asset/fonts/jetbrains_mono.ttf",
            isExternal = false,
        )
    ),
    FIRA_CODE(
        font = FontModel(
            fontUuid = "fira_code",
            fontName = "Fira Code",
            fontPath = "file:///android_asset/fonts/fira_code.ttf",
            isExternal = false,
        )
    ),
    SOURCE_CODE_PRO(
        font = FontModel(
            fontUuid = "source_code_pro",
            fontName = "Source Code Pro",
            fontPath = "file:///android_asset/fonts/source_code_pro.ttf",
            isExternal = false,
        )
    ),
    ANONYMOUS_PRO(
        font = FontModel(
            fontUuid = "anonymous_pro",
            fontName = "Anonymous Pro",
            fontPath = "file:///android_asset/fonts/anonymous_pro.ttf",
            isExternal = false,
        )
    ),
    DEJAVU_SANS_MONO(
        font = FontModel(
            fontUuid = "dejavu_sans_mono",
            fontName = "DejaVu Sans Mono",
            fontPath = "file:///android_asset/fonts/dejavu_sans_mono.ttf",
            isExternal = false,
        )
    );

    companion object {

        fun find(path: String): FontModel? {
            return values().find { it.font.fontPath == path }?.font
        }
    }
}