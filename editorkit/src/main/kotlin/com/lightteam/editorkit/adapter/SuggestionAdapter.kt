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

abstract class SuggestionAdapter(
    context: Context,
    resourceId: Int
) : ArrayAdapter<SuggestionModel>(context, resourceId) {

    var colorScheme: ColorScheme? = null
    var wordsManager: WordsManager? = null

    private var queryText = ""

    abstract fun createViewHolder(parent: ViewGroup): SuggestionViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = createViewHolder(parent)
        viewHolder.bind(getItem(position), queryText)
        return viewHolder.itemView
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            private val suggestions: MutableList<SuggestionModel> = mutableListOf()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                suggestions.clear()
                wordsManager?.let {
                    val query = constraint.toString()
                    for (suggestion in it.suggestions) {
                        val word = suggestion.text.toString()
                        if (word.startsWith(query, ignoreCase = true) &&
                            !word.equals(query, ignoreCase = true)) {
                            queryText = query
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
                addAll(suggestions)
                notifyDataSetChanged()
            }
        }
    }

    abstract class SuggestionViewHolder(val itemView: View) {
        abstract fun bind(suggestion: SuggestionModel?, query: String)
    }
}