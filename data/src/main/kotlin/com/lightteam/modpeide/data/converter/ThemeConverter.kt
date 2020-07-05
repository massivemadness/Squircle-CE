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

import androidx.core.graphics.toColorInt
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.modpeide.data.model.theme.ExternalScheme
import com.lightteam.modpeide.data.model.theme.ExternalTheme
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.domain.model.theme.ThemeModel
import java.util.*

object ThemeConverter {

    fun toModel(themeEntity: ThemeEntity): ThemeModel {
        return ThemeModel(
            uuid = themeEntity.uuid,
            name = themeEntity.name,
            author = themeEntity.author,
            description = themeEntity.description,
            isExternal = themeEntity.isExternal,
            isPaid = themeEntity.isPaid,
            colorScheme = ColorScheme(
                textColor = themeEntity.textColor.toColorInt(),
                backgroundColor = themeEntity.backgroundColor.toColorInt(),
                gutterColor = themeEntity.gutterColor.toColorInt(),
                gutterDividerColor = themeEntity.gutterDividerColor.toColorInt(),
                gutterCurrentLineNumberColor = themeEntity.gutterCurrentLineNumberColor.toColorInt(),
                gutterTextColor = themeEntity.gutterTextColor.toColorInt(),
                selectedLineColor = themeEntity.selectedLineColor.toColorInt(),
                selectionColor = themeEntity.selectionColor.toColorInt(),
                suggestionQueryColor = themeEntity.suggestionQueryColor.toColorInt(),
                findResultBackgroundColor = themeEntity.findResultBackgroundColor.toColorInt(),
                delimiterBackgroundColor = themeEntity.delimiterBackgroundColor.toColorInt(),
                numberColor = themeEntity.numberColor.toColorInt(),
                operatorColor = themeEntity.operatorColor.toColorInt(),
                keywordColor = themeEntity.keywordColor.toColorInt(),
                typeColor = themeEntity.typeColor.toColorInt(),
                langConstColor = themeEntity.langConstColor.toColorInt(),
                methodColor = themeEntity.methodColor.toColorInt(),
                stringColor = themeEntity.stringColor.toColorInt(),
                commentColor = themeEntity.commentColor.toColorInt()
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

    fun toModel(externalTheme: ExternalTheme?): ThemeModel {
        return ThemeModel(
            uuid = externalTheme?.uuid ?: UUID.randomUUID().toString(),
            name = externalTheme?.name ?: "",
            author = externalTheme?.author ?: "",
            description = externalTheme?.description ?: "",
            isExternal = externalTheme?.isExternal ?: true,
            isPaid = externalTheme?.isPaid ?: true,
            colorScheme = ColorScheme(
                textColor = (externalTheme?.externalScheme?.textColor ?: "#000000").toColorInt(),
                backgroundColor = (externalTheme?.externalScheme?.backgroundColor ?: "#000000").toColorInt(),
                gutterColor = (externalTheme?.externalScheme?.gutterColor ?: "#000000").toColorInt(),
                gutterDividerColor = (externalTheme?.externalScheme?.gutterDividerColor ?: "#000000").toColorInt(),
                gutterCurrentLineNumberColor = (externalTheme?.externalScheme?.gutterCurrentLineNumberColor ?: "#000000").toColorInt(),
                gutterTextColor = (externalTheme?.externalScheme?.gutterTextColor ?: "#000000").toColorInt(),
                selectedLineColor = (externalTheme?.externalScheme?.selectedLineColor ?: "#000000").toColorInt(),
                selectionColor = (externalTheme?.externalScheme?.selectionColor ?: "#000000").toColorInt(),
                suggestionQueryColor = (externalTheme?.externalScheme?.suggestionQueryColor ?: "#000000").toColorInt(),
                findResultBackgroundColor = (externalTheme?.externalScheme?.findResultBackgroundColor ?: "#000000").toColorInt(),
                delimiterBackgroundColor = (externalTheme?.externalScheme?.delimiterBackgroundColor ?: "#000000").toColorInt(),
                numberColor = (externalTheme?.externalScheme?.numberColor ?: "#000000").toColorInt(),
                operatorColor = (externalTheme?.externalScheme?.operatorColor ?: "#000000").toColorInt(),
                keywordColor = (externalTheme?.externalScheme?.keywordColor ?: "#000000").toColorInt(),
                typeColor = (externalTheme?.externalScheme?.typeColor ?: "#000000").toColorInt(),
                langConstColor = (externalTheme?.externalScheme?.langConstColor ?: "#000000").toColorInt(),
                methodColor = (externalTheme?.externalScheme?.methodColor ?: "#000000").toColorInt(),
                stringColor = (externalTheme?.externalScheme?.stringColor ?: "#000000").toColorInt(),
                commentColor = (externalTheme?.externalScheme?.commentColor ?: "#000000").toColorInt()
            )
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