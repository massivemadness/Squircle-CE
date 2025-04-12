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

package com.blacksquircle.ui.feature.themes.data.model

import android.graphics.Color
import com.blacksquircle.ui.feature.themes.domain.model.ColorModel

internal object EditorTheme {

    val DARCULA = ColorModel(
        textColor = Color.parseColor("#ABB7C5"),
        backgroundColor = Color.parseColor("#1E1F22"),
        numberColor = Color.parseColor("#6897BB"),
        operatorColor = Color.parseColor("#E8E2B7"),
        keywordColor = Color.parseColor("#EC7600"),
        variableColor = Color.parseColor("#9378A7"),
        functionColor = Color.parseColor("#FEC76C"),
        stringColor = Color.parseColor("#6E875A"),
        commentColor = Color.parseColor("#66747B"),
    )

    val MONOKAI = ColorModel(
        textColor = Color.parseColor("#F8F8F8"),
        backgroundColor = Color.parseColor("#272823"),
        numberColor = Color.parseColor("#BB8FF8"),
        operatorColor = Color.parseColor("#F8F8F2"),
        keywordColor = Color.parseColor("#EB347E"),
        variableColor = Color.parseColor("#7FD0E4"),
        functionColor = Color.parseColor("#B6E951"),
        stringColor = Color.parseColor("#EBE48C"),
        commentColor = Color.parseColor("#89826D"),
    )

    val OBSIDIAN = ColorModel(
        textColor = Color.parseColor("#E0E2E4"),
        backgroundColor = Color.parseColor("#2A3134"),
        numberColor = Color.parseColor("#F8CE4E"),
        operatorColor = Color.parseColor("#E7E2BC"),
        keywordColor = Color.parseColor("#9EC56F"),
        variableColor = Color.parseColor("#6E8BAE"),
        functionColor = Color.parseColor("#E7E2BC"),
        stringColor = Color.parseColor("#DE7C2E"),
        commentColor = Color.parseColor("#808C92"),
    )

    val LADIES_NIGHT = ColorModel(
        textColor = Color.parseColor("#E0E2E4"),
        backgroundColor = Color.parseColor("#22282C"),
        numberColor = Color.parseColor("#7EFBFD"),
        operatorColor = Color.parseColor("#E7E2BC"),
        keywordColor = Color.parseColor("#DA89A2"),
        variableColor = Color.parseColor("#6EA4C7"),
        functionColor = Color.parseColor("#8FB4C5"),
        stringColor = Color.parseColor("#75D367"),
        commentColor = Color.parseColor("#808C92"),
    )

    val TOMORROW_NIGHT = ColorModel(
        textColor = Color.parseColor("#C6C8C6"),
        backgroundColor = Color.parseColor("#222426"),
        numberColor = Color.parseColor("#D49668"),
        operatorColor = Color.parseColor("#CFD1CF"),
        keywordColor = Color.parseColor("#AD95B8"),
        variableColor = Color.parseColor("#EAC780"),
        functionColor = Color.parseColor("#87A1BB"),
        stringColor = Color.parseColor("#B7BC73"),
        commentColor = Color.parseColor("#969896"),
    )

    val VISUAL_STUDIO = ColorModel(
        textColor = Color.parseColor("#C8C8C8"),
        backgroundColor = Color.parseColor("#232323"),
        numberColor = Color.parseColor("#BACDAB"),
        operatorColor = Color.parseColor("#DCDCDC"),
        keywordColor = Color.parseColor("#669BD1"),
        variableColor = Color.parseColor("#9DDDFF"),
        functionColor = Color.parseColor("#71C6B1"),
        stringColor = Color.parseColor("#CE9F89"),
        commentColor = Color.parseColor("#6BA455"),
    )

    val INTELLIJ_LIGHT = ColorModel(
        textColor = Color.parseColor("#000000"),
        backgroundColor = Color.parseColor("#FFFFFF"),
        numberColor = Color.parseColor("#284FE2"),
        operatorColor = Color.parseColor("#000000"),
        keywordColor = Color.parseColor("#1232AC"),
        variableColor = Color.parseColor("#7C1E8F"),
        functionColor = Color.parseColor("#286077"),
        stringColor = Color.parseColor("#377B2A"),
        commentColor = Color.parseColor("#8C8C8C"),
    )

    val SOLARIZED_LIGHT = ColorModel(
        textColor = Color.parseColor("#697A82"),
        backgroundColor = Color.parseColor("#FCF6E5"),
        numberColor = Color.parseColor("#BC5429"),
        operatorColor = Color.parseColor("#697A82"),
        keywordColor = Color.parseColor("#89982E"),
        variableColor = Color.parseColor("#6D71BE"),
        functionColor = Color.parseColor("#C24480"),
        stringColor = Color.parseColor("#519F98"),
        commentColor = Color.parseColor("#96A0A1"),
    )

    val ECLIPSE = ColorModel(
        textColor = Color.parseColor("#000000"),
        backgroundColor = Color.parseColor("#FFFFFF"),
        numberColor = Color.parseColor("#0000F5"),
        operatorColor = Color.parseColor("#000000"),
        keywordColor = Color.parseColor("#800055"),
        variableColor = Color.parseColor("#5D1776"),
        functionColor = Color.parseColor("#000000"),
        stringColor = Color.parseColor("#2602F5"),
        commentColor = Color.parseColor("#4F7E61"),
    )
}