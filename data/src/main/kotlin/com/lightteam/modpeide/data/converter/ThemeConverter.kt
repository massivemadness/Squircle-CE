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
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.modpeide.data.model.theme.ExternalScheme
import com.lightteam.modpeide.data.model.theme.ExternalTheme
import com.lightteam.modpeide.domain.model.theme.ThemeModel
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.database.entity.theme.ThemeEntity

object ThemeConverter {
    
    fun toExternalTheme(themeModel: ThemeModel): ExternalTheme {
        return ExternalTheme(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description,
            isExternal = themeModel.isExternal,
            isPaid = themeModel.isPaid,
            externalScheme = ExternalScheme(
                textColor = themeModel.colorScheme.textColor.toHexString(),
                backgroundColor = themeModel.colorScheme.backgroundColor.toHexString(),
                gutterColor = themeModel.colorScheme.gutterColor.toHexString(),
                gutterDividerColor = themeModel.colorScheme.gutterDividerColor.toHexString(),
                gutterCurrentLineNumberColor = themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString(),
                gutterTextColor = themeModel.colorScheme.gutterTextColor.toHexString(),
                selectedLineColor = themeModel.colorScheme.selectedLineColor.toHexString(),
                selectionColor = themeModel.colorScheme.selectionColor.toHexString(),
                suggestionQueryColor = themeModel.colorScheme.suggestionQueryColor.toHexString(),
                findResultBackgroundColor = themeModel.colorScheme.findResultBackgroundColor.toHexString(),
                delimiterBackgroundColor = themeModel.colorScheme.delimiterBackgroundColor.toHexString(),
                numberColor = themeModel.colorScheme.numberColor.toHexString(),
                operatorColor = themeModel.colorScheme.operatorColor.toHexString(),
                keywordColor = themeModel.colorScheme.keywordColor.toHexString(),
                typeColor = themeModel.colorScheme.typeColor.toHexString(),
                langConstColor = themeModel.colorScheme.langConstColor.toHexString(),
                methodColor = themeModel.colorScheme.methodColor.toHexString(),
                stringColor = themeModel.colorScheme.stringColor.toHexString(),
                commentColor = themeModel.colorScheme.commentColor.toHexString()
            )
        )
    }

    fun toModel(themeEntity: ThemeEntity): ThemeModel {
        return ThemeModel(
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

    fun toEntity(themeModel: ThemeModel): ThemeEntity {
        return ThemeEntity(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description,
            isExternal = themeModel.isExternal,
            isPaid = themeModel.isPaid,
            textColor = themeModel.colorScheme.textColor.toHexString(),
            backgroundColor = themeModel.colorScheme.backgroundColor.toHexString(),
            gutterColor = themeModel.colorScheme.gutterColor.toHexString(),
            gutterDividerColor = themeModel.colorScheme.gutterDividerColor.toHexString(),
            gutterCurrentLineNumberColor = themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString(),
            gutterTextColor = themeModel.colorScheme.gutterTextColor.toHexString(),
            selectedLineColor = themeModel.colorScheme.selectedLineColor.toHexString(),
            selectionColor = themeModel.colorScheme.selectionColor.toHexString(),
            suggestionQueryColor = themeModel.colorScheme.suggestionQueryColor.toHexString(),
            findResultBackgroundColor = themeModel.colorScheme.findResultBackgroundColor.toHexString(),
            delimiterBackgroundColor = themeModel.colorScheme.delimiterBackgroundColor.toHexString(),
            numberColor = themeModel.colorScheme.numberColor.toHexString(),
            operatorColor = themeModel.colorScheme.operatorColor.toHexString(),
            keywordColor = themeModel.colorScheme.keywordColor.toHexString(),
            typeColor = themeModel.colorScheme.typeColor.toHexString(),
            langConstColor = themeModel.colorScheme.langConstColor.toHexString(),
            methodColor = themeModel.colorScheme.methodColor.toHexString(),
            stringColor = themeModel.colorScheme.stringColor.toHexString(),
            commentColor = themeModel.colorScheme.commentColor.toHexString()
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

    fun toSyntaxScheme(themeModel: ThemeModel): SyntaxScheme {
        return SyntaxScheme(
            numberColor = themeModel.colorScheme.numberColor,
            operatorColor = themeModel.colorScheme.operatorColor,
            keywordColor = themeModel.colorScheme.keywordColor,
            typeColor = themeModel.colorScheme.typeColor,
            langConstColor = themeModel.colorScheme.langConstColor,
            methodColor = themeModel.colorScheme.methodColor,
            stringColor = themeModel.colorScheme.stringColor,
            commentColor = themeModel.colorScheme.commentColor
        )
    }
}