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

package com.blacksquircle.ui.feature.editor.ui.fragment.view

import com.blacksquircle.ui.core.extensions.showToast
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

internal var Content.scrollX: Int
    get() = (this as? TextContent)?.scrollX ?: 0
    set(value) {
        (this as? TextContent)?.scrollX = value
    }

internal var Content.scrollY: Int
    get() = (this as? TextContent)?.scrollY ?: 0
    set(value) {
        (this as? TextContent)?.scrollY = value
    }

internal val Content.selectionStart: Int
    get() = cursor.left

internal val Content.selectionEnd: Int
    get() = cursor.right

internal fun CodeEditor.syncScroll() {
    scroller.startScroll(0, 0, text.scrollX, text.scrollY)
    scroller.abortAnimation()
}

internal fun CodeEditor.createFromRegistry(): EditorColorScheme {
    return try {
        TextMateColorScheme.create(ThemeRegistry.getInstance())
    } catch (e: Exception) {
        context.showToast(text = "Couldn't load theme from registry: ${e.message}")
        EditorColorScheme()
    }
}

internal fun CodeEditor.createFromRegistry(language: String, codeCompletion: Boolean): Language {
    return try {
        TextMateLanguage.create(language, codeCompletion)
    } catch (e: Exception) {
        context.showToast(text = "Couldn't load grammar from registry: ${e.message}")
        EmptyLanguage()
    }
}