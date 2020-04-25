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
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.lightteam.language.model.SuggestionModel
import com.lightteam.language.scheme.ColorScheme
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.feature.suggestion.WordsManager
import java.util.*

class SuggestionAdapter(context: Context, resourceId: Int) : ArrayAdapter<SuggestionModel>(context, resourceId) {

    private val suggestions: MutableList<SuggestionModel> = mutableListOf() // Отображаемый список

    private lateinit var wordsManager: WordsManager
    private lateinit var colorScheme: ColorScheme

    private var query = ""

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            suggestions.clear()
            val name = constraint.toString().toLowerCase(Locale.getDefault())
            for (suggestion in wordsManager.getSuggestions()) {
                val suggestionText = suggestion.text.toString().toLowerCase(Locale.getDefault())
                if (suggestionText.startsWith(name) && suggestionText != name) {
                    query = name
                    suggestions.add(suggestion)
                }
            }
            filterResults.values = suggestions
            filterResults.count = suggestions.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            if (suggestions.isNotEmpty()) {
                addAll(suggestions)
            }
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = SuggestionViewHolder.create(parent, colorScheme.filterableColor.toColorInt())
        viewHolder.bind(getItem(position), query)
        return viewHolder.itemView
    }

    override fun getFilter(): Filter {
        return filter
    }

    fun setWordsManager(wordsManager: WordsManager) {
        this.wordsManager = wordsManager
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        this.colorScheme = colorScheme
    }

    class SuggestionViewHolder(
        val itemView: View,
        private val itemColor: Int
    ) {

        companion object {
            fun create(parent: ViewGroup, itemColor: Int): SuggestionViewHolder {
                val itemView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_suggestion, parent, false)
                return SuggestionViewHolder(itemView, itemColor)
            }
        }

        private val textView: TextView = itemView.findViewById(R.id.item_title)

        fun bind(suggestion: SuggestionModel?, query: String) {
            val spannable = SpannableStringBuilder(suggestion?.text)
            spannable.setSpan(
                ForegroundColorSpan(itemColor),
                0, query.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        }
    }
}