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

package com.lightteam.editorkit.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.lightteam.editorkit.feature.colorscheme.ColorScheme
import com.lightteam.editorkit.feature.suggestions.WordsManager
import com.lightteam.language.model.SuggestionModel
import java.util.*

abstract class SuggestionAdapter(
    context: Context,
    resourceId: Int
) : ArrayAdapter<SuggestionModel>(context, resourceId) {

    var colorScheme: ColorScheme? = null
    var wordsManager: WordsManager? = null

    private val suggestions: MutableList<SuggestionModel> = mutableListOf()

    private var queryText = ""

    abstract fun createViewHolder(parent: ViewGroup): SuggestionViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = createViewHolder(parent)
        viewHolder.bind(getItem(position), queryText)
        return viewHolder.itemView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                suggestions.clear()
                if (wordsManager != null) {
                    val name = constraint.toString().toLowerCase(Locale.getDefault())
                    for (suggestion in wordsManager!!.getSuggestions()) {
                        val suggestionText = suggestion.text.toString().toLowerCase(Locale.getDefault())
                        if (suggestionText.startsWith(name) && suggestionText != name) {
                            queryText = name
                            suggestions.add(suggestion)
                        }
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
    }

    abstract class SuggestionViewHolder(val itemView: View) {
        abstract fun bind(suggestion: SuggestionModel?, query: String)
    }
}