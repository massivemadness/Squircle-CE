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

package com.blacksquircle.ui.feature.editor.ui.editor.view

import android.graphics.Color
import com.blacksquircle.ui.feature.themes.api.model.ThemeType
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry.ThemeChangeListener
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula
import org.eclipse.tm4e.core.internal.theme.raw.IRawTheme
import org.eclipse.tm4e.core.internal.theme.raw.RawTheme
import timber.log.Timber
import org.eclipse.tm4e.core.internal.theme.Theme as TextMateTheme

internal class SquircleScheme private constructor(
    private val themeRegistry: ThemeRegistry,
    themeModel: ThemeModel,
) : EditorColorScheme(), ThemeChangeListener {

    private var currentTheme: ThemeModel = themeModel
    private var rawTheme: IRawTheme? = themeModel.rawTheme
    private var theme: TextMateTheme? = themeModel.theme

    fun setTheme(themeModel: ThemeModel) {
        super.colors.clear()
        this.currentTheme = themeModel
        this.rawTheme = themeModel.rawTheme
        this.theme = themeModel.theme
        applyDefault()
    }

    override fun onChangeTheme(newTheme: ThemeModel) {
        setTheme(newTheme)
    }

    override fun applyDefault() {
        if (rawTheme == null) return
        if (theme == null) return

        super.applyDefault()

        if (!themeRegistry.hasListener(this)) {
            themeRegistry.addListener(this)
        }

        val rawTheme = rawTheme as? RawTheme ?: return
        val rawSubTheme = rawTheme["colors"] as? RawTheme ?: return
        applySQTheme(rawSubTheme)
    }

    private fun applySQTheme(rawTheme: RawTheme) {
        setColor(HIGHLIGHTED_DELIMITERS_UNDERLINE, Color.TRANSPARENT)

        val background = rawTheme["editor.background"] as? String
        if (background != null) {
            setColor(WHOLE_BACKGROUND, Color.parseColor(background))
        }
        val foreground = rawTheme["editor.foreground"] as? String
        if (foreground != null) {
            setColor(TEXT_NORMAL, Color.parseColor(foreground))
        }
        val whitespace = rawTheme["editor.whitespace"] as? String
        if (whitespace != null) {
            setColor(NON_PRINTABLE_CHAR, Color.parseColor(whitespace))
        }
        val cursor = rawTheme["editor.cursor"] as? String
        if (cursor != null) {
            setColor(SELECTION_INSERT, Color.parseColor(cursor))
        }
        val handle = rawTheme["editor.handle"] as? String
        if (handle != null) {
            setColor(SELECTION_HANDLE, Color.parseColor(handle))
        }
        val selectionBackground = rawTheme["editor.selection.background"] as? String
        if (selectionBackground != null) {
            setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor(selectionBackground))
        }
        val currentLineBackground = rawTheme["editor.currentLine.background"] as? String
        if (currentLineBackground != null) {
            setColor(CURRENT_LINE, Color.parseColor(currentLineBackground))
        }
        val delimitersBackground = rawTheme["editor.highlightedDelimiters.background"] as? String
        if (delimitersBackground != null) {
            setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, Color.parseColor(delimitersBackground))
        }
        val delimitersForeground = rawTheme["editor.highlightedDelimiters.foreground"] as? String
        if (delimitersForeground != null) {
            setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(delimitersForeground))
        }
        val matchHighlightBackground = rawTheme["editor.matchHighlight.background"] as? String
        if (matchHighlightBackground != null) {
            setColor(MATCHED_TEXT_BACKGROUND, Color.parseColor(matchHighlightBackground))
        }
        val lineNumberDivider = rawTheme["editor.lineNumber.divider"] as? String
        if (lineNumberDivider != null) {
            setColor(LINE_DIVIDER, Color.parseColor(lineNumberDivider))
        }
        val lineNumberBackground = rawTheme["editor.lineNumber.background"] as? String
        if (lineNumberBackground != null) {
            setColor(LINE_NUMBER_BACKGROUND, Color.parseColor(lineNumberBackground))
        }
        val lineNumberForeground = rawTheme["editor.lineNumber.foreground"] as? String
        if (lineNumberForeground != null) {
            setColor(LINE_NUMBER, Color.parseColor(lineNumberForeground))
        }
        val lineNumberActiveForeground = rawTheme["editor.lineNumber.activeForeground"] as? String
        if (lineNumberActiveForeground != null) {
            setColor(LINE_NUMBER_CURRENT, Color.parseColor(lineNumberActiveForeground))
        }
        val indentGuideBackground = rawTheme["editor.indentGuide.background"] as? String
        if (indentGuideBackground != null) {
            setColor(BLOCK_LINE, Color.parseColor(indentGuideBackground))
        }
        val indentGuideActiveBackground = rawTheme["editor.indentGuide.activeBackground"] as? String
        if (indentGuideActiveBackground != null) {
            setColor(BLOCK_LINE_CURRENT, Color.parseColor(indentGuideActiveBackground))
        }
        val popupWindowCorner = rawTheme["editor.popupWindow.corner"] as? String
        if (popupWindowCorner != null) {
            setColor(COMPLETION_WND_CORNER, Color.parseColor(popupWindowCorner))
        }
        val popupWindowBackground = rawTheme["editor.popupWindow.background"] as? String
        if (popupWindowBackground != null) {
            val color = Color.parseColor(popupWindowBackground)
            setColor(COMPLETION_WND_BACKGROUND, color)
            setColor(TEXT_ACTION_WINDOW_BACKGROUND, color)
            setColor(DIAGNOSTIC_TOOLTIP_BACKGROUND, color)
        }
        val popupWindowActiveBackground = rawTheme["editor.popupWindow.activeBackground"] as? String
        if (popupWindowActiveBackground != null) {
            setColor(COMPLETION_WND_ITEM_CURRENT, Color.parseColor(popupWindowActiveBackground))
        }
    }

    override fun isDark(): Boolean {
        val rawTheme = rawTheme as? RawTheme ?: return false
        val rawType = rawTheme["type"] as String? ?: return false
        return rawType == ThemeType.DARK.value
    }

    override fun getColor(type: Int): Int {
        if (type >= 255) {
            // Cache colors in super class
            val superColor = super.getColor(type)
            if (superColor == 0) {
                if (theme != null) {
                    val color = try {
                        theme?.getColor(type - 255)
                    } catch (e: IndexOutOfBoundsException) {
                        return super.getColor(TEXT_NORMAL)
                    }
                    val isDefault = color.equals("@default", ignoreCase = true)
                    val newColor = if (color != null && !isDefault) {
                        Color.parseColor(color)
                    } else {
                        super.getColor(TEXT_NORMAL)
                    }
                    super.colors.put(type, newColor)
                    return newColor
                }
                return super.getColor(TEXT_NORMAL)
            } else {
                return superColor
            }
        }
        return super.getColor(type)
    }

    override fun detachEditor(editor: CodeEditor) {
        super.detachEditor(editor)
        themeRegistry.removeListener(this)
    }

    override fun attachEditor(editor: CodeEditor) {
        super.attachEditor(editor)
        try {
            themeRegistry.loadTheme(currentTheme)
        } catch (e: Exception) {
            // ignored
        }
        setTheme(currentTheme)
    }

    companion object {

        fun create(): EditorColorScheme {
            return try {
                val themeRegistry = ThemeRegistry.getInstance()
                val theme = themeRegistry.currentThemeModel
                SquircleScheme(themeRegistry, theme)
            } catch (e: Exception) {
                Timber.e("Couldn't load theme from registry: ${e.message}")
                SchemeDarcula()
            }
        }
    }
}