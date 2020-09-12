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

package com.lightteam.modpeide.ui.editor.adapters

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lightteam.editorkit.adapter.SuggestionAdapter
import com.lightteam.language.base.model.SuggestionModel
import com.lightteam.modpeide.R

class AutoCompleteAdapter(context: Context) : SuggestionAdapter(context, R.layout.item_suggestion) {

    override fun createViewHolder(parent: ViewGroup): SuggestionViewHolder {
        return BasicSuggestionViewHolder.create(
            parent,
            colorScheme?.suggestionQueryColor ?: Color.WHITE
        )
    }

    class BasicSuggestionViewHolder(
        itemView: View,
        private val itemColor: Int
    ) : SuggestionViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup, itemColor: Int): SuggestionViewHolder {
                val itemView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_suggestion, parent, false)
                return BasicSuggestionViewHolder(itemView, itemColor)
            }
        }

        private val textView: TextView = itemView.findViewById(R.id.item_title)

        override fun bind(suggestion: SuggestionModel?, query: String) {
            val spannable = SpannableStringBuilder(suggestion?.text)
            if (query.length < spannable.length) {
                spannable.setSpan(
                    ForegroundColorSpan(itemColor),
                    0, query.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.text = spannable
        }
    }
}