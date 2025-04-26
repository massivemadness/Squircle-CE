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

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import io.github.rosemoe.sora.R
import io.github.rosemoe.sora.widget.component.CompletionLayout
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import timber.log.Timber

/**
 * Changes:
 * - Fixed corner stroke width (1 dp)
 */
internal class SquircleCompletion : CompletionLayout {

    private var listView: ListView? = null
    private var rootView: LinearLayout? = null

    private var editorAutoCompletion: EditorAutoCompletion? = null

    override fun setEditorCompletion(completion: EditorAutoCompletion) {
        editorAutoCompletion = completion
    }

    override fun setEnabledAnimation(enabledAnimation: Boolean) = Unit

    override fun inflate(context: Context): View {
        rootView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    context.resources.displayMetrics
                )
            }
            setRootViewOutlineProvider(this)
        }
        listView = ListView(context).apply {
            dividerHeight = 0
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                try {
                    editorAutoCompletion?.select(position)
                } catch (e: Exception) {
                    Timber.e(e, e.message)
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        rootView?.addView(listView, LinearLayout.LayoutParams(-1, -1))
        return rootView!!
    }

    override fun onApplyColorScheme(colorScheme: EditorColorScheme) {
        rootView?.background = GradientDrawable().apply {
            cornerRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                editorAutoCompletion!!.context.resources.displayMetrics
            )
            val strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                editorAutoCompletion!!.context.resources.displayMetrics
            )
            setStroke(
                strokeWidth.toInt(),
                colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER)
            )
            setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND))
        }
        rootView?.let(::setRootViewOutlineProvider)
    }

    override fun setLoading(state: Boolean) = Unit
    override fun getCompletionList(): ListView = listView!!

    private fun performScrollList(offset: Int) {
        val adpView = completionList

        val down = SystemClock.uptimeMillis()
        var ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        adpView.onTouchEvent(ev)
        ev.recycle()

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_MOVE, 0f, offset.toFloat(), 0)
        adpView.onTouchEvent(ev)
        ev.recycle()

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_CANCEL, 0f, offset.toFloat(), 0)
        adpView.onTouchEvent(ev)
        ev.recycle()
    }

    private fun setRootViewOutlineProvider(rootView: View) {
        rootView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0,
                    0,
                    view.width,
                    view.height,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.context.resources.displayMetrics,
                    )
                )
            }
        }
        rootView.clipToOutline = true
    }

    override fun ensureListPositionVisible(position: Int, increment: Int) {
        listView?.post {
            while (listView!!.firstVisiblePosition + 1 > position && listView!!.canScrollList(-1)) {
                performScrollList(increment / 2)
            }
            while (listView!!.lastVisiblePosition - 1 < position && listView!!.canScrollList(1)) {
                performScrollList(-increment / 2)
            }
        }
    }

    /**
     * Changes:
     * - Changed typeface to MONOSPACE
     * - Hidden description
     * - Updated paddings
     */
    class Adapter : EditorCompletionAdapter() {

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?,
            isCurrentCursorPosition: Boolean
        ): View {
            val item = getItem(position)
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.default_completion_result_item, parent, false)
            view.tag = position

            view.findViewById<ImageView>(R.id.result_item_image).apply {
                setImageDrawable(item.icon)
            }
            view.findViewById<TextView>(R.id.result_item_label).apply {
                text = item.label
                setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY))
                setTypeface(Typeface.MONOSPACE)

                val verticalPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 6f, context.resources.displayMetrics
                ).toInt()
                val horizontalPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics
                ).toInt()

                updatePadding(
                    left = horizontalPadding,
                    right = horizontalPadding,
                    top = verticalPadding,
                    bottom = verticalPadding,
                )
            }
            view.findViewById<TextView>(R.id.result_item_desc).apply {
                text = item.desc
                setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY))
                isVisible = false
            }

            if (isCurrentCursorPosition) {
                view.setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT))
            } else {
                view.setBackgroundColor(Color.TRANSPARENT)
            }
            return view
        }

        override fun getItemHeight(): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                45f,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}