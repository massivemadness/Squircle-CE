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

package com.blacksquircle.ui.feature.themes.data.mapper

import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.core.storage.database.entity.theme.ThemeEntity
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.editorkit.model.ColorScheme
import com.blacksquircle.ui.feature.themes.data.model.ExternalScheme
import com.blacksquircle.ui.feature.themes.data.model.ExternalTheme
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import java.util.*

internal object ThemeMapper {

    const val FALLBACK_COLOR = "#000000"

    fun toModel(themeEntity: ThemeEntity): ThemeModel {
        return ThemeModel(
            uuid = themeEntity.uuid,
            name = themeEntity.name,
            author = themeEntity.author,
            description = themeEntity.description,
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = themeEntity.textColor.toColorInt(),
                cursorColor = themeEntity.cursorColor.toColorInt(),
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
                preprocessorColor = themeEntity.preprocessorColor.toColorInt(),
                variableColor = themeEntity.variableColor.toColorInt(),
                methodColor = themeEntity.methodColor.toColorInt(),
                stringColor = themeEntity.stringColor.toColorInt(),
                commentColor = themeEntity.commentColor.toColorInt(),
                tagColor = themeEntity.tagColor.toColorInt(),
                tagNameColor = themeEntity.tagNameColor.toColorInt(),
                attrNameColor = themeEntity.attrNameColor.toColorInt(),
                attrValueColor = themeEntity.attrValueColor.toColorInt(),
                entityRefColor = themeEntity.entityRefColor.toColorInt(),
            ),
        )
    }

    fun toEntity(themeModel: ThemeModel): ThemeEntity {
        return ThemeEntity(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description,
            textColor = themeModel.colorScheme.textColor.toHexString(),
            cursorColor = themeModel.colorScheme.cursorColor.toHexString(),
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
            preprocessorColor = themeModel.colorScheme.preprocessorColor.toHexString(),
            variableColor = themeModel.colorScheme.variableColor.toHexString(),
            methodColor = themeModel.colorScheme.methodColor.toHexString(),
            stringColor = themeModel.colorScheme.stringColor.toHexString(),
            commentColor = themeModel.colorScheme.commentColor.toHexString(),
            tagColor = themeModel.colorScheme.tagColor.toHexString(),
            tagNameColor = themeModel.colorScheme.tagNameColor.toHexString(),
            attrNameColor = themeModel.colorScheme.attrNameColor.toHexString(),
            attrValueColor = themeModel.colorScheme.attrValueColor.toHexString(),
            entityRefColor = themeModel.colorScheme.entityRefColor.toHexString(),
        )
    }

    fun toExternalTheme(themeModel: ThemeModel): ExternalTheme {
        return ExternalTheme(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description,
            externalScheme = ExternalScheme(
                textColor = themeModel.colorScheme.textColor.toHexString(),
                cursorColor = themeModel.colorScheme.cursorColor.toHexString(),
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
                preprocessorColor = themeModel.colorScheme.preprocessorColor.toHexString(),
                variableColor = themeModel.colorScheme.variableColor.toHexString(),
                methodColor = themeModel.colorScheme.methodColor.toHexString(),
                stringColor = themeModel.colorScheme.stringColor.toHexString(),
                commentColor = themeModel.colorScheme.commentColor.toHexString(),
                tagColor = themeModel.colorScheme.tagColor.toHexString(),
                tagNameColor = themeModel.colorScheme.tagNameColor.toHexString(),
                attrNameColor = themeModel.colorScheme.attrNameColor.toHexString(),
                attrValueColor = themeModel.colorScheme.attrValueColor.toHexString(),
                entityRefColor = themeModel.colorScheme.entityRefColor.toHexString(),
            ),
        )
    }

    fun toModel(externalTheme: ExternalTheme?): ThemeModel {
        return ThemeModel(
            uuid = externalTheme?.uuid ?: UUID.randomUUID().toString(),
            name = externalTheme?.name.orEmpty(),
            author = externalTheme?.author.orEmpty(),
            description = externalTheme?.description.orEmpty(),
            isExternal = true,
            colorScheme = ColorScheme(
                textColor = (externalTheme?.externalScheme?.textColor ?: FALLBACK_COLOR).toColorInt(),
                cursorColor = (externalTheme?.externalScheme?.cursorColor ?: FALLBACK_COLOR).toColorInt(),
                backgroundColor = (externalTheme?.externalScheme?.backgroundColor ?: FALLBACK_COLOR).toColorInt(),
                gutterColor = (externalTheme?.externalScheme?.gutterColor ?: FALLBACK_COLOR).toColorInt(),
                gutterDividerColor = (externalTheme?.externalScheme?.gutterDividerColor ?: FALLBACK_COLOR).toColorInt(),
                gutterCurrentLineNumberColor = (externalTheme?.externalScheme?.gutterCurrentLineNumberColor ?: FALLBACK_COLOR).toColorInt(),
                gutterTextColor = (externalTheme?.externalScheme?.gutterTextColor ?: FALLBACK_COLOR).toColorInt(),
                selectedLineColor = (externalTheme?.externalScheme?.selectedLineColor ?: FALLBACK_COLOR).toColorInt(),
                selectionColor = (externalTheme?.externalScheme?.selectionColor ?: FALLBACK_COLOR).toColorInt(),
                suggestionQueryColor = (externalTheme?.externalScheme?.suggestionQueryColor ?: FALLBACK_COLOR).toColorInt(),
                findResultBackgroundColor = (externalTheme?.externalScheme?.findResultBackgroundColor ?: FALLBACK_COLOR).toColorInt(),
                delimiterBackgroundColor = (externalTheme?.externalScheme?.delimiterBackgroundColor ?: FALLBACK_COLOR).toColorInt(),
                numberColor = (externalTheme?.externalScheme?.numberColor ?: FALLBACK_COLOR).toColorInt(),
                operatorColor = (externalTheme?.externalScheme?.operatorColor ?: FALLBACK_COLOR).toColorInt(),
                keywordColor = (externalTheme?.externalScheme?.keywordColor ?: FALLBACK_COLOR).toColorInt(),
                typeColor = (externalTheme?.externalScheme?.typeColor ?: FALLBACK_COLOR).toColorInt(),
                langConstColor = (externalTheme?.externalScheme?.langConstColor ?: FALLBACK_COLOR).toColorInt(),
                preprocessorColor = (externalTheme?.externalScheme?.preprocessorColor ?: FALLBACK_COLOR).toColorInt(),
                variableColor = (externalTheme?.externalScheme?.variableColor ?: FALLBACK_COLOR).toColorInt(),
                methodColor = (externalTheme?.externalScheme?.methodColor ?: FALLBACK_COLOR).toColorInt(),
                stringColor = (externalTheme?.externalScheme?.stringColor ?: FALLBACK_COLOR).toColorInt(),
                commentColor = (externalTheme?.externalScheme?.commentColor ?: FALLBACK_COLOR).toColorInt(),
                tagColor = (externalTheme?.externalScheme?.tagColor ?: FALLBACK_COLOR).toColorInt(),
                tagNameColor = (externalTheme?.externalScheme?.tagNameColor ?: FALLBACK_COLOR).toColorInt(),
                attrNameColor = (externalTheme?.externalScheme?.attrNameColor ?: FALLBACK_COLOR).toColorInt(),
                attrValueColor = (externalTheme?.externalScheme?.attrValueColor ?: FALLBACK_COLOR).toColorInt(),
                entityRefColor = (externalTheme?.externalScheme?.entityRefColor ?: FALLBACK_COLOR).toColorInt(),
            ),
        )
    }
}