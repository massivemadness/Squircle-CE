package com.brackeys.ui.editorkit.model

import android.graphics.Typeface

data class EditorConfig(

    // Font
    var fontSize: Float = 14f,
    var fontType: Typeface = Typeface.MONOSPACE,

    // Editor
    var wordWrap: Boolean = true,
    var codeCompletion: Boolean = true,
    var pinchZoom: Boolean = true,
    var highlightCurrentLine: Boolean = true,
    var highlightDelimiters: Boolean = true,

    // Keyboard
    var softKeyboard: Boolean = false,

    // Code Style
    var autoIndentation: Boolean = true,
    var autoCloseBrackets: Boolean = true,
    var autoCloseQuotes: Boolean = true,
    var useSpacesInsteadOfTabs: Boolean = true,
    var tabWidth: Int = 4
)