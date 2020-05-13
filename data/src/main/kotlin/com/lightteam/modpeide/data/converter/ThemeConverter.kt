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
import com.lightteam.modpeide.data.feature.scheme.external.ExternalScheme
import com.lightteam.modpeide.data.feature.scheme.external.ExternalTheme
import com.lightteam.modpeide.data.feature.scheme.internal.ColorScheme
import com.lightteam.modpeide.data.feature.scheme.internal.Theme
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.database.entity.theme.ThemeEntity

object ThemeConverter {
    
    fun toExternalTheme(theme: Theme): ExternalTheme {
        return ExternalTheme(
            uuid = theme.uuid,
            name = theme.name,
            author = theme.author,
            description = theme.description,
            isExternal = theme.isExternal,
            isPaid = theme.isPaid,
            externalScheme = ExternalScheme(
                textColor = theme.colorScheme.textColor.toHexString(),
                backgroundColor = theme.colorScheme.backgroundColor.toHexString(),
                gutterColor = theme.colorScheme.gutterColor.toHexString(),
                gutterDividerColor = theme.colorScheme.gutterDividerColor.toHexString(),
                gutterCurrentLineNumberColor = theme.colorScheme.gutterCurrentLineNumberColor.toHexString(),
                gutterTextColor = theme.colorScheme.gutterTextColor.toHexString(),
                selectedLineColor = theme.colorScheme.selectedLineColor.toHexString(),
                selectionColor = theme.colorScheme.selectionColor.toHexString(),
                suggestionQueryColor = theme.colorScheme.suggestionQueryColor.toHexString(),
                findResultBackgroundColor = theme.colorScheme.findResultBackgroundColor.toHexString(),
                delimiterBackgroundColor = theme.colorScheme.delimiterBackgroundColor.toHexString(),
                numberColor = theme.colorScheme.numberColor.toHexString(),
                operatorColor = theme.colorScheme.operatorColor.toHexString(),
                keywordColor = theme.colorScheme.keywordColor.toHexString(),
                typeColor = theme.colorScheme.typeColor.toHexString(),
                langConstColor = theme.colorScheme.langConstColor.toHexString(),
                methodColor = theme.colorScheme.methodColor.toHexString(),
                stringColor = theme.colorScheme.stringColor.toHexString(),
                commentColor = theme.colorScheme.commentColor.toHexString()
            )
        )
    }

    fun toModel(themeEntity: ThemeEntity): Theme {
        return Theme(
            uuid = themeEntity.uuid,
            name = themeEntity.name,
            author = themeEntity.author,
            description = themeEntity.description,
            isExternal = themeEntity.isExternal,
            isPaid = themeEntity.isPaid,
            colorScheme = ColorScheme(
                textColor = Color.parseColor(themeEntity.textColor),
                backgroundColor = Color.parseColor(themeEntity.backgroundColor),
                gutterColor = Color.parseColor(themeEntity.gutterColor),
                gutterDividerColor = Color.parseColor(themeEntity.gutterDividerColor),
                gutterCurrentLineNumberColor = Color.parseColor(themeEntity.gutterCurrentLineNumberColor),
                gutterTextColor = Color.parseColor(themeEntity.gutterTextColor),
                selectedLineColor = Color.parseColor(themeEntity.selectedLineColor),
                selectionColor = Color.parseColor(themeEntity.selectionColor),
                suggestionQueryColor = Color.parseColor(themeEntity.suggestionQueryColor),
                findResultBackgroundColor = Color.parseColor(themeEntity.findResultBackgroundColor),
                delimiterBackgroundColor = Color.parseColor(themeEntity.delimiterBackgroundColor),
                numberColor = Color.parseColor(themeEntity.numberColor),
                operatorColor = Color.parseColor(themeEntity.operatorColor),
                keywordColor = Color.parseColor(themeEntity.keywordColor),
                typeColor = Color.parseColor(themeEntity.typeColor),
                langConstColor = Color.parseColor(themeEntity.langConstColor),
                methodColor = Color.parseColor(themeEntity.methodColor),
                stringColor = Color.parseColor(themeEntity.stringColor),
                commentColor = Color.parseColor(themeEntity.commentColor)
            )
        )
    }

    fun toEntity(theme: Theme): ThemeEntity {
        return ThemeEntity(
            uuid = theme.uuid,
            name = theme.name,
            author = theme.author,
            description = theme.description,
            isExternal = theme.isExternal,
            isPaid = theme.isPaid,
            textColor = theme.colorScheme.textColor.toHexString(),
            backgroundColor = theme.colorScheme.backgroundColor.toHexString(),
            gutterColor = theme.colorScheme.gutterColor.toHexString(),
            gutterDividerColor = theme.colorScheme.gutterDividerColor.toHexString(),
            gutterCurrentLineNumberColor = theme.colorScheme.gutterCurrentLineNumberColor.toHexString(),
            gutterTextColor = theme.colorScheme.gutterTextColor.toHexString(),
            selectedLineColor = theme.colorScheme.selectedLineColor.toHexString(),
            selectionColor = theme.colorScheme.selectionColor.toHexString(),
            suggestionQueryColor = theme.colorScheme.suggestionQueryColor.toHexString(),
            findResultBackgroundColor = theme.colorScheme.findResultBackgroundColor.toHexString(),
            delimiterBackgroundColor = theme.colorScheme.delimiterBackgroundColor.toHexString(),
            numberColor = theme.colorScheme.numberColor.toHexString(),
            operatorColor = theme.colorScheme.operatorColor.toHexString(),
            keywordColor = theme.colorScheme.keywordColor.toHexString(),
            typeColor = theme.colorScheme.typeColor.toHexString(),
            langConstColor = theme.colorScheme.langConstColor.toHexString(),
            methodColor = theme.colorScheme.methodColor.toHexString(),
            stringColor = theme.colorScheme.stringColor.toHexString(),
            commentColor = theme.colorScheme.commentColor.toHexString()
        )
    }

    fun toEntity(externalTheme: ExternalTheme): ThemeEntity {
        return ThemeEntity(
            uuid = externalTheme.uuid,
            name = externalTheme.name,
            author = externalTheme.author,
            description = externalTheme.description,
            isExternal = externalTheme.isExternal,
            isPaid = externalTheme.isPaid,
            textColor = externalTheme.externalScheme.textColor,
            backgroundColor = externalTheme.externalScheme.backgroundColor,
            gutterColor = externalTheme.externalScheme.gutterColor,
            gutterDividerColor = externalTheme.externalScheme.gutterDividerColor,
            gutterCurrentLineNumberColor = externalTheme.externalScheme.gutterCurrentLineNumberColor,
            gutterTextColor = externalTheme.externalScheme.gutterTextColor,
            selectedLineColor = externalTheme.externalScheme.selectedLineColor,
            selectionColor = externalTheme.externalScheme.selectionColor,
            suggestionQueryColor = externalTheme.externalScheme.suggestionQueryColor,
            findResultBackgroundColor = externalTheme.externalScheme.findResultBackgroundColor,
            delimiterBackgroundColor = externalTheme.externalScheme.delimiterBackgroundColor,
            numberColor = externalTheme.externalScheme.numberColor,
            operatorColor = externalTheme.externalScheme.operatorColor,
            keywordColor = externalTheme.externalScheme.keywordColor,
            typeColor = externalTheme.externalScheme.typeColor,
            langConstColor = externalTheme.externalScheme.langConstColor,
            methodColor = externalTheme.externalScheme.methodColor,
            stringColor = externalTheme.externalScheme.stringColor,
            commentColor = externalTheme.externalScheme.commentColor
        )
    }

    fun toSyntaxScheme(theme: Theme): SyntaxScheme {
        return SyntaxScheme(
            numberColor = theme.colorScheme.numberColor,
            operatorColor = theme.colorScheme.operatorColor,
            keywordColor = theme.colorScheme.keywordColor,
            typeColor = theme.colorScheme.typeColor,
            langConstColor = theme.colorScheme.langConstColor,
            methodColor = theme.colorScheme.methodColor,
            stringColor = theme.colorScheme.stringColor,
            commentColor = theme.colorScheme.commentColor
        )
    }
}