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

package com.blacksquircle.ui.editorkit.theme

import android.graphics.Color
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.language.base.model.SyntaxScheme

object EditorTheme {

    val DARCULA = ColorScheme(
        textColor = Color.parseColor("#ABB7C5"),
        backgroundColor = Color.parseColor("#303030"),
        gutterColor = Color.parseColor("#313335"),
        gutterDividerColor = Color.parseColor("#555555"),
        gutterCurrentLineNumberColor = Color.parseColor("#A4A3A3"),
        gutterTextColor = Color.parseColor("#616366"),
        selectedLineColor = Color.parseColor("#3A3A3A"),
        selectionColor = Color.parseColor("#28427F"),
        suggestionQueryColor = Color.parseColor("#987DAC"),
        findResultBackgroundColor = Color.parseColor("#33654B"),
        delimiterBackgroundColor = Color.parseColor("#33654B"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#6897BB"),
            operatorColor = Color.parseColor("#E8E2B7"),
            keywordColor = Color.parseColor("#EC7600"),
            typeColor = Color.parseColor("#EC7600"),
            langConstColor = Color.parseColor("#EC7600"),
            preprocessorColor = Color.parseColor("#C9C54E"),
            variableColor = Color.parseColor("#9378A7"),
            methodColor = Color.parseColor("#FEC76C"),
            stringColor = Color.parseColor("#6E875A"),
            commentColor = Color.parseColor("#66747B"),
            tagColor = Color.parseColor("#E2C077"),
            tagNameColor = Color.parseColor("#E2C077"),
            attrNameColor = Color.parseColor("#BABABA"),
            attrValueColor = Color.parseColor("#ABC16D"),
            entityRefColor = Color.parseColor("#6897BB")
        )
    )

    val MONOKAI = ColorScheme(
        textColor = Color.parseColor("#F8F8F8"),
        backgroundColor = Color.parseColor("#272823"),
        gutterColor = Color.parseColor("#272823"),
        gutterDividerColor = Color.parseColor("#5B5A4F"),
        gutterCurrentLineNumberColor = Color.parseColor("#C8BBAC"),
        gutterTextColor = Color.parseColor("#5B5A4F"),
        selectedLineColor = Color.parseColor("#34352D"),
        selectionColor = Color.parseColor("#666666"),
        suggestionQueryColor = Color.parseColor("#7CE0F3"),
        findResultBackgroundColor = Color.parseColor("#5F5E5A"),
        delimiterBackgroundColor = Color.parseColor("#5F5E5A"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#BB8FF8"),
            operatorColor = Color.parseColor("#F8F8F2"),
            keywordColor = Color.parseColor("#EB347E"),
            typeColor = Color.parseColor("#7FD0E4"),
            langConstColor = Color.parseColor("#EB347E"),
            preprocessorColor = Color.parseColor("#EB347E"),
            variableColor = Color.parseColor("#7FD0E4"),
            methodColor = Color.parseColor("#B6E951"),
            stringColor = Color.parseColor("#EBE48C"),
            commentColor = Color.parseColor("#89826D"),
            tagColor = Color.parseColor("#F8F8F8"),
            tagNameColor = Color.parseColor("#EB347E"),
            attrNameColor = Color.parseColor("#B6E951"),
            attrValueColor = Color.parseColor("#EBE48C"),
            entityRefColor = Color.parseColor("#BB8FF8")
        )
    )

    val OBSIDIAN = ColorScheme(
        textColor = Color.parseColor("#E0E2E4"),
        backgroundColor = Color.parseColor("#2A3134"),
        gutterColor = Color.parseColor("#2A3134"),
        gutterDividerColor = Color.parseColor("#67777B"),
        gutterCurrentLineNumberColor = Color.parseColor("#E0E0E0"),
        gutterTextColor = Color.parseColor("#859599"),
        selectedLineColor = Color.parseColor("#31393C"),
        selectionColor = Color.parseColor("#616161"),
        suggestionQueryColor = Color.parseColor("#9EC56F"),
        findResultBackgroundColor = Color.parseColor("#838177"),
        delimiterBackgroundColor = Color.parseColor("#616161"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#F8CE4E"),
            operatorColor = Color.parseColor("#E7E2BC"),
            keywordColor = Color.parseColor("#9EC56F"),
            typeColor = Color.parseColor("#9EC56F"),
            langConstColor = Color.parseColor("#9EC56F"),
            preprocessorColor = Color.parseColor("#9B84B9"),
            variableColor = Color.parseColor("#6E8BAE"),
            methodColor = Color.parseColor("#E7E2BC"),
            stringColor = Color.parseColor("#DE7C2E"),
            commentColor = Color.parseColor("#808C92"),
            tagColor = Color.parseColor("#E7E2BC"),
            tagNameColor = Color.parseColor("#9EC56F"),
            attrNameColor = Color.parseColor("#E0E2E4"),
            attrValueColor = Color.parseColor("#DE7C2E"),
            entityRefColor = Color.parseColor("#F8CE4E")
        )
    )

    val LADIES_NIGHT = ColorScheme(
        textColor = Color.parseColor("#E0E2E4"),
        backgroundColor = Color.parseColor("#22282C"),
        gutterColor = Color.parseColor("#2A3134"),
        gutterDividerColor = Color.parseColor("#4F575A"),
        gutterCurrentLineNumberColor = Color.parseColor("#E0E2E4"),
        gutterTextColor = Color.parseColor("#859599"),
        selectedLineColor = Color.parseColor("#373340"),
        selectionColor = Color.parseColor("#5B2B41"),
        suggestionQueryColor = Color.parseColor("#6E8BAE"),
        findResultBackgroundColor = Color.parseColor("#8A4364"),
        delimiterBackgroundColor = Color.parseColor("#616161"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#7EFBFD"),
            operatorColor = Color.parseColor("#E7E2BC"),
            keywordColor = Color.parseColor("#DA89A2"),
            typeColor = Color.parseColor("#DA89A2"),
            langConstColor = Color.parseColor("#DA89A2"),
            preprocessorColor = Color.parseColor("#9B84B9"),
            variableColor = Color.parseColor("#6EA4C7"),
            methodColor = Color.parseColor("#8FB4C5"),
            stringColor = Color.parseColor("#75D367"),
            commentColor = Color.parseColor("#808C92"),
            tagColor = Color.parseColor("#E7E2BC"),
            tagNameColor = Color.parseColor("#DA89A2"),
            attrNameColor = Color.parseColor("#E0E2E4"),
            attrValueColor = Color.parseColor("#75D367"),
            entityRefColor = Color.parseColor("#7EFBFD")
        )
    )

    val TOMORROW_NIGHT = ColorScheme(
        textColor = Color.parseColor("#C6C8C6"),
        backgroundColor = Color.parseColor("#222426"),
        gutterColor = Color.parseColor("#222426"),
        gutterDividerColor = Color.parseColor("#4B4D51"),
        gutterCurrentLineNumberColor = Color.parseColor("#FFFFFF"),
        gutterTextColor = Color.parseColor("#C6C8C6"),
        selectedLineColor = Color.parseColor("#2D2F33"),
        selectionColor = Color.parseColor("#383B40"),
        suggestionQueryColor = Color.parseColor("#EAC780"),
        findResultBackgroundColor = Color.parseColor("#4B4E54"),
        delimiterBackgroundColor = Color.parseColor("#616161"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#D49668"),
            operatorColor = Color.parseColor("#CFD1CF"),
            keywordColor = Color.parseColor("#AD95B8"),
            typeColor = Color.parseColor("#AD95B8"),
            langConstColor = Color.parseColor("#AD95B8"),
            preprocessorColor = Color.parseColor("#CFD1CF"),
            variableColor = Color.parseColor("#EAC780"),
            methodColor = Color.parseColor("#87A1BB"),
            stringColor = Color.parseColor("#B7BC73"),
            commentColor = Color.parseColor("#969896"),
            tagColor = Color.parseColor("#CFD1CF"),
            tagNameColor = Color.parseColor("#AD95B8"),
            attrNameColor = Color.parseColor("#C6C8C6"),
            attrValueColor = Color.parseColor("#B7BC73"),
            entityRefColor = Color.parseColor("#D49668")
        )
    )

    val VISUAL_STUDIO_2013 = ColorScheme(
        textColor = Color.parseColor("#C8C8C8"),
        backgroundColor = Color.parseColor("#232323"),
        gutterColor = Color.parseColor("#2C2C2C"),
        gutterDividerColor = Color.parseColor("#555555"),
        gutterCurrentLineNumberColor = Color.parseColor("#FFFFFF"),
        gutterTextColor = Color.parseColor("#C6C8C6"),
        selectedLineColor = Color.parseColor("#141414"),
        selectionColor = Color.parseColor("#454464"),
        suggestionQueryColor = Color.parseColor("#4F98F7"),
        findResultBackgroundColor = Color.parseColor("#1C3D6B"),
        delimiterBackgroundColor = Color.parseColor("#616161"),
        syntaxScheme = SyntaxScheme(
            numberColor = Color.parseColor("#BACDAB"),
            operatorColor = Color.parseColor("#DCDCDC"),
            keywordColor = Color.parseColor("#669BD1"),
            typeColor = Color.parseColor("#669BD1"),
            langConstColor = Color.parseColor("#669BD1"),
            preprocessorColor = Color.parseColor("#C49594"),
            variableColor = Color.parseColor("#9DDDFF"),
            methodColor = Color.parseColor("#71C6B1"),
            stringColor = Color.parseColor("#CE9F89"),
            commentColor = Color.parseColor("#6BA455"),
            tagColor = Color.parseColor("#DCDCDC"),
            tagNameColor = Color.parseColor("#669BD1"),
            attrNameColor = Color.parseColor("#C8C8C8"),
            attrValueColor = Color.parseColor("#CE9F89"),
            entityRefColor = Color.parseColor("#BACDAB")
        )
    )
}