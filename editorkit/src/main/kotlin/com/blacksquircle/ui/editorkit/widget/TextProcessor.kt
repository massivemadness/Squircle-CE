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

package com.blacksquircle.ui.editorkit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.text.PrecomputedTextCompat
import com.blacksquircle.ui.editorkit.plugin.base.EditorPlugin
import com.blacksquircle.ui.editorkit.plugin.base.PluginContainer
import com.blacksquircle.ui.editorkit.plugin.base.PluginSupplier
import com.blacksquircle.ui.editorkit.widget.internal.SyntaxHighlightEditText

open class TextProcessor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle
) : SyntaxHighlightEditText(context, attrs, defStyleAttr), PluginContainer {

    companion object {
        private const val TAG = "TextProcessor"
    }

    private val plugins = hashSetOf<EditorPlugin>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (plugin in plugins) {
            plugin.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (plugin in plugins) {
            plugin.onLayout(changed, left, top, right, bottom)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        for (plugin in plugins) {
            plugin.beforeDraw(canvas)
        }
        super.onDraw(canvas)
        for (plugin in plugins) {
            plugin.afterDraw(canvas)
        }
    }

    override fun onColorSchemeChanged() {
        super.onColorSchemeChanged()
        for (plugin in plugins) {
            plugin.onColorSchemeChanged(colorScheme)
        }
    }

    override fun onLanguageChanged() {
        super.onLanguageChanged()
        for (plugin in plugins) {
            plugin.onLanguageChanged(language)
        }
    }

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        for (plugin in plugins) {
            plugin.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for (plugin in plugins) {
            plugin.onSizeChanged(w, h, oldw, oldh)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        post {
            for (plugin in plugins) {
                plugin.onSelectionChanged(selStart, selEnd)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        for (plugin in plugins) {
            if (plugin.onTouchEvent(event)) {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        for (plugin in plugins) {
            if (plugin.onKeyUp(keyCode, event)) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        for (plugin in plugins) {
            if (plugin.onKeyDown(keyCode, event)) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        super.doBeforeTextChanged(text, start, count, after)
        for (plugin in plugins) {
            plugin.beforeTextChanged(text, start, count, after)
        }
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.doOnTextChanged(text, start, before, count)
        for (plugin in plugins) {
            plugin.onTextChanged(text, start, before, count)
        }
    }

    override fun replaceText(newStart: Int, newEnd: Int, newText: CharSequence) {
        super.replaceText(newStart, newEnd, newText)
        for (plugin in plugins) {
            plugin.onTextReplaced(newStart, newEnd, newText)
        }
    }

    override fun doAfterTextChanged(text: Editable?) {
        super.doAfterTextChanged(text)
        for (plugin in plugins) {
            plugin.afterTextChanged(text)
        }
    }

    override fun addLine(lineNumber: Int, lineStart: Int, lineLength: Int) {
        super.addLine(lineNumber, lineStart, lineLength)
        for (plugin in plugins) {
            plugin.addLine(lineNumber, lineStart, lineLength)
        }
    }

    override fun removeLine(lineNumber: Int) {
        super.removeLine(lineNumber)
        for (plugin in plugins) {
            plugin.removeLine(lineNumber)
        }
    }

    override fun setTextContent(textParams: PrecomputedTextCompat) {
        for (plugin in plugins) {
            plugin.clearLines()
        }
        super.setTextContent(textParams)
        for (plugin in plugins) {
            plugin.setTextContent(textParams)
        }
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        post {
            for (plugin in plugins) {
                plugin.setTextSize(size)
            }
        }
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        post {
            for (plugin in plugins) {
                plugin.setTypeface(tf)
            }
        }
    }

    override fun clearText() {
        super.clearText()
        for (plugin in plugins) {
            plugin.setEmptyText()
        }
    }

    override fun showDropDown() {
        super.showDropDown()
        for (plugin in plugins) {
            plugin.showDropDown()
        }
    }

    override fun plugins(supplier: PluginSupplier) {
        val allPlugins = plugins union supplier.supply()
        val crossPlugins = allPlugins intersect supplier.supply()
        val disjointPlugins = allPlugins subtract crossPlugins
        for (plugin in disjointPlugins) {
            uninstallPlugin(plugin.pluginId)
        }
        for (plugin in supplier.supply()) {
            installPlugin(plugin)
        }
    }

    override fun <T : EditorPlugin> installPlugin(plugin: T) {
        if (!hasPlugin(plugin.pluginId)) {
            plugins.add(plugin)
            plugin.onAttached(this)
        } else {
            Log.e(TAG, "Plugin $plugin is already attached.")
        }
    }

    override fun uninstallPlugin(pluginId: String) {
        if (hasPlugin(pluginId)) {
            findPlugin<EditorPlugin>(pluginId)?.let { plugin ->
                plugins.remove(plugin)
                plugin.onDetached(this)
            }
        } else {
            Log.e(TAG, "Plugin $pluginId is not attached.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : EditorPlugin> findPlugin(pluginId: String): T? {
        return plugins.find { it.pluginId == pluginId } as? T
    }

    override fun hasPlugin(pluginId: String): Boolean {
        return plugins.any { it.pluginId == pluginId }
    }
}