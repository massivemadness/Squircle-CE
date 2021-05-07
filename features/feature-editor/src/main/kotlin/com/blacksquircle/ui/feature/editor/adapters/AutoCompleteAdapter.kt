/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.editor.adapters

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.blacksquircle.ui.editorkit.adapter.SuggestionAdapter
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.databinding.ItemSuggestionBinding
import com.blacksquircle.ui.language.base.model.Suggestion

class AutoCompleteAdapter(context: Context) : SuggestionAdapter(context, R.layout.item_suggestion) {

    override fun createViewHolder(parent: ViewGroup): SuggestionViewHolder {
        return AutoCompleteViewHolder.create(
            parent,
            colorScheme?.suggestionQueryColor ?: Color.WHITE
        )
    }

    class AutoCompleteViewHolder(
        private val binding: ItemSuggestionBinding,
        private val queryColor: Int
    ) : SuggestionViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, queryColor: Int): SuggestionViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemSuggestionBinding.inflate(inflater, parent, false)
                return AutoCompleteViewHolder(binding, queryColor)
            }
        }

        override fun bind(suggestion: Suggestion?, query: String) {
            if (suggestion != null) {
                val spannable = SpannableString(suggestion.text)
                if (query.length < spannable.length) {
                    spannable.setSpan(
                        ForegroundColorSpan(queryColor),
                        0, query.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                binding.itemType.isVisible = suggestion.type != Suggestion.Type.NONE

                binding.itemType.text = suggestion.type.value
                binding.itemSuggestion.text = spannable
                binding.itemReturnType?.text = suggestion.returnType
            }
        }
    }
}