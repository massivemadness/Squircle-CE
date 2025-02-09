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

package com.blacksquircle.ui.feature.fonts.api.model

enum class InternalFont(val font: FontModel) {
    JETBRAINS_MONO(
        font = FontModel(
            uuid = "jetbrains_mono",
            name = "JetBrains Mono",
            path = "file:///android_asset/fonts/jetbrains_mono.ttf",
            isExternal = false,
        )
    ),
    DROID_SANS_MONO(
        font = FontModel(
            uuid = "droid_sans_mono",
            name = "Droid Sans Mono",
            path = "file:///android_asset/fonts/droid_sans_mono.ttf",
            isExternal = false,
        )
    ),
    FIRA_CODE(
        font = FontModel(
            uuid = "fira_code",
            name = "Fira Code",
            path = "file:///android_asset/fonts/fira_code.ttf",
            isExternal = false,
        )
    ),
    SOURCE_CODE_PRO(
        font = FontModel(
            uuid = "source_code_pro",
            name = "Source Code Pro",
            path = "file:///android_asset/fonts/source_code_pro.ttf",
            isExternal = false,
        )
    ),
    ANONYMOUS_PRO(
        font = FontModel(
            uuid = "anonymous_pro",
            name = "Anonymous Pro",
            path = "file:///android_asset/fonts/anonymous_pro.ttf",
            isExternal = false,
        )
    ),
    DEJAVU_SANS_MONO(
        font = FontModel(
            uuid = "dejavu_sans_mono",
            name = "DejaVu Sans Mono",
            path = "file:///android_asset/fonts/dejavu_sans_mono.ttf",
            isExternal = false,
        )
    );

    companion object {

        fun find(uuid: String): FontModel? {
            return entries.find { it.font.uuid == uuid }?.font
        }
    }
}