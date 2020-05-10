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

package com.lightteam.modpeide.data.converter

import android.graphics.Color
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.modpeide.data.feature.scheme.ColorScheme
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.database.entity.theme.ThemeEntity

object ThemeConverter {

    fun toModel(entity: ThemeEntity): Theme {
        return Theme(
            uuid = entity.uuid,
            name = entity.name,
            author = entity.author,
            description = entity.description,
            isExternal = entity.isExternal,
            isPaid = entity.isPaid,
            colorScheme = ColorScheme(
                textColor = Color.parseColor(entity.textColor),
                backgroundColor = Color.parseColor(entity.backgroundColor),
                gutterColor = Color.parseColor(entity.gutterColor),
                gutterDividerColor = Color.parseColor(entity.gutterDividerColor),
                gutterCurrentLineNumberColor = Color.parseColor(entity.gutterCurrentLineNumberColor),
                gutterTextColor = Color.parseColor(entity.gutterTextColor),
                selectedLineColor = Color.parseColor(entity.selectedLineColor),
                selectionColor = Color.parseColor(entity.selectionColor),
                suggestionMatchColor = Color.parseColor(entity.suggestionMatchColor),
                searchBackgroundColor = Color.parseColor(entity.searchBackgroundColor),
                delimitersBackgroundColor = Color.parseColor(entity.delimitersBackgroundColor),
                numberColor = Color.parseColor(entity.numberColor),
                operatorColor = Color.parseColor(entity.operatorColor),
                bracketColor = Color.parseColor(entity.bracketColor),
                keywordColor = Color.parseColor(entity.keywordColor),
                typeColor = Color.parseColor(entity.typeColor),
                langConstColor = Color.parseColor(entity.langConstColor),
                methodColor = Color.parseColor(entity.methodColor),
                stringColor = Color.parseColor(entity.stringColor),
                commentColor = Color.parseColor(entity.commentColor)
            )
        )
    }

    fun toSyntaxScheme(theme: Theme): SyntaxScheme {
        return SyntaxScheme(
            numberColor = theme.colorScheme.numberColor,
            operatorColor = theme.colorScheme.operatorColor,
            bracketColor = theme.colorScheme.bracketColor,
            keywordColor = theme.colorScheme.keywordColor,
            typeColor = theme.colorScheme.typeColor,
            langConstColor = theme.colorScheme.langConstColor,
            methodColor = theme.colorScheme.methodColor,
            stringColor = theme.colorScheme.stringColor,
            commentColor = theme.colorScheme.commentColor
        )
    }
}