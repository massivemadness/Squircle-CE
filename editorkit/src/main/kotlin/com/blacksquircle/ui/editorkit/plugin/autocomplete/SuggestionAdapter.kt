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

package com.blacksquircle.ui.editorkit.plugin.autocomplete

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.blacksquircle.ui.language.base.model.Suggestion
import com.blacksquircle.ui.language.base.provider.SuggestionProvider

abstract class SuggestionAdapter(
    context: Context,
    resourceId: Int,
) : ArrayAdapter<Suggestion>(context, resourceId) {

    private var queryText: String? = null
    private var suggestionProvider: SuggestionProvider? = null

    abstract fun createViewHolder(parent: ViewGroup): SuggestionViewHolder

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = createViewHolder(parent)
        viewHolder.bind(getItem(position), queryText ?: "")
        return viewHolder.itemView
    }

    override fun getFilter() = object : Filter() {

        private val suggestions = mutableListOf<Suggestion>()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            suggestions.clear()
            suggestionProvider?.let { provider ->
                val query = constraint.toString()
                for (suggestion in provider.getAll()) {
                    val word = suggestion.toString()
                    if (word.startsWith(query, ignoreCase = true) &&
                        !word.equals(query, ignoreCase = true)
                    ) {
                        queryText = query
                        suggestions.add(suggestion)
                    }
                }
            }
            return FilterResults().apply {
                values = suggestions
                count = suggestions.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            addAll(suggestions)
            notifyDataSetChanged()
        }
    }

    fun setSuggestionProvider(suggestionProvider: SuggestionProvider) {
        this.suggestionProvider = suggestionProvider
    }

    abstract class SuggestionViewHolder(val itemView: View) {
        abstract fun bind(suggestion: Suggestion?, query: String)
    }
}