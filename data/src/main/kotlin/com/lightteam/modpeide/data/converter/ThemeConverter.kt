package com.lightteam.modpeide.data.converter

import android.graphics.Color
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.modpeide.data.feature.scheme.ColorScheme
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.data.model.entity.ThemeEntity

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
                bracketBackgroundColor = Color.parseColor(entity.bracketBackgroundColor),
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