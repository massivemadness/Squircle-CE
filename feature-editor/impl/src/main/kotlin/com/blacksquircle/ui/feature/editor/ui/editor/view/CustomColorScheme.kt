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
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry.ThemeChangeListener
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.internal.theme.Theme
import org.eclipse.tm4e.core.internal.theme.raw.IRawTheme
import org.eclipse.tm4e.core.internal.theme.raw.RawTheme

internal class CustomColorScheme(
    private val themeRegistry: ThemeRegistry,
    themeModel: ThemeModel,
) : EditorColorScheme(), ThemeChangeListener {

    private var currentTheme: ThemeModel = themeModel
    private var rawTheme: IRawTheme? = themeModel.rawTheme
    private var theme: Theme? = themeModel.theme

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
        applyVSCTheme(rawSubTheme)
    }

    /**
     * Added:
     * - editor.selectionHighlightBackground
     * - highlightedDelimitersBackground
     * - editorLineNumber.divider
     * - editorHandle.foreground
     * - completionWindowCorner
     * Removed:
     * - tooltipBackground
     * - tooltipBriefMessageColor
     * - tooltipDetailedMessageColor
     * - tooltipActionColor
     */
    private fun applyVSCTheme(rawTheme: RawTheme) {
        setColor(HIGHLIGHTED_DELIMITERS_UNDERLINE, Color.TRANSPARENT)

        val background = rawTheme["editor.background"] as String?
        if (background != null) {
            setColor(WHOLE_BACKGROUND, Color.parseColor(background))
            setColor(LINE_NUMBER_BACKGROUND, Color.parseColor(background))
        }
        val foreground = rawTheme["editor.foreground"] as String?
        if (foreground != null) {
            setColor(TEXT_NORMAL, Color.parseColor(foreground))
        }
        val selection = rawTheme["editor.selectionBackground"] as String?
        if (selection != null) {
            setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor(selection))
        }
        val selectionHighlight = rawTheme["editor.selectionHighlightBackground"] as String?
        if (selectionHighlight != null) {
            setColor(MATCHED_TEXT_BACKGROUND, Color.parseColor(selectionHighlight))
        }
        val delimiterBackground = rawTheme["editor.highlightedDelimitersBackground"] as String?
        if (delimiterBackground != null) {
            setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, Color.parseColor(delimiterBackground))
        }
        val delimiterForeground = rawTheme["editor.highlightedDelimitersForeground"] as String?
        if (delimiterForeground != null) {
            setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(delimiterForeground))
        }
        val lineHighlight = rawTheme["editor.lineHighlightBackground"] as String?
        if (lineHighlight != null) {
            setColor(CURRENT_LINE, Color.parseColor(lineHighlight))
        }
        val lineHighlightBackground = rawTheme["editorLineNumber.foreground"] as String?
        if (lineHighlightBackground != null) {
            setColor(LINE_NUMBER, Color.parseColor(lineHighlightBackground))
        }
        val lineHighlightActiveForeground = rawTheme["editorLineNumber.activeForeground"] as String?
        if (lineHighlightActiveForeground != null) {
            setColor(LINE_NUMBER_CURRENT, Color.parseColor(lineHighlightActiveForeground))
        }
        val lineDivider = rawTheme["editorLineNumber.divider"] as String?
        if (lineDivider != null) {
            setColor(LINE_DIVIDER, Color.parseColor(lineDivider))
        }
        val cursor = rawTheme["editorCursor.foreground"] as String?
        if (cursor != null) {
            setColor(SELECTION_INSERT, Color.parseColor(cursor))
        }
        val handle = rawTheme["editorHandle.foreground"] as String?
        if (handle != null) {
            setColor(SELECTION_HANDLE, Color.parseColor(handle))
        }
        val invisibles = rawTheme["editorWhitespace.foreground"] as String?
        if (invisibles != null) {
            setColor(NON_PRINTABLE_CHAR, Color.parseColor(invisibles))
        }
        val completionWindowCorner = rawTheme["editor.completionWindowCorner"] as String?
        if (completionWindowCorner != null) {
            setColor(COMPLETION_WND_CORNER, Color.parseColor(completionWindowCorner))
        }
        val completionBackground = rawTheme["editor.completionWindowBackground"] as String?
        if (completionBackground != null) {
            val color = Color.parseColor(completionBackground)
            setColor(COMPLETION_WND_BACKGROUND, color)
            setColor(TEXT_ACTION_WINDOW_BACKGROUND, color)
            setColor(DIAGNOSTIC_TOOLTIP_BACKGROUND, color)
        }
        val completionBackgroundCurrent = rawTheme["editor.completionWindowBackgroundCurrent"] as String?
        if (completionBackgroundCurrent != null) {
            setColor(COMPLETION_WND_ITEM_CURRENT, Color.parseColor(completionBackgroundCurrent))
        }
        val editorIndentBackground = rawTheme["editorIndentGuide.background"] as String?
        val blockLineColor =
            ((getColor(WHOLE_BACKGROUND) + getColor(TEXT_NORMAL)) / 2) and 0x00FFFFFF or -0x78000000
        val blockLineColorCur = (blockLineColor) or -0x1000000
        if (editorIndentBackground != null) {
            setColor(BLOCK_LINE, Color.parseColor(editorIndentBackground))
        } else {
            setColor(BLOCK_LINE, blockLineColor)
        }
        val editorIndentActiveBackground = rawTheme["editorIndentGuide.activeBackground"] as String?
        if (editorIndentActiveBackground != null) {
            setColor(BLOCK_LINE_CURRENT, Color.parseColor(editorIndentActiveBackground))
        } else {
            setColor(BLOCK_LINE_CURRENT, blockLineColorCur)
        }
    }

    /**
     * Get value from theme file
     */
    override fun isDark(): Boolean {
        val rawTheme = rawTheme as? RawTheme ?: return false
        val rawType = rawTheme["type"] as String? ?: return false
        return rawType == "dark"
    }

    override fun getColor(type: Int): Int {
        if (type >= 255) {
            // Cache colors in super class
            val superColor = super.getColor(type)
            if (superColor == 0) {
                if (theme != null) {
                    val color = theme?.getColor(type - 255)
                    val newColor = if (color != null) {
                        Color.parseColor(color)
                    } else {
                        super.getColor(
                            TEXT_NORMAL
                        )
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

        fun create(): CustomColorScheme {
            val themeRegistry = ThemeRegistry.getInstance()
            val theme = themeRegistry.currentThemeModel
            return CustomColorScheme(themeRegistry, theme)
        }
    }
}