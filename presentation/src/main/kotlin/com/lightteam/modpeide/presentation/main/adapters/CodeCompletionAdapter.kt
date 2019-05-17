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

package com.lightteam.modpeide.presentation.main.adapters

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.lightteam.modpeide.R

class CodeCompletionAdapter(context: Context, resourceId: Int) : ArrayAdapter<String>(context, resourceId) {

    var color: Int = Color.WHITE

    var dataSet: MutableList<String> = mutableListOf() // Полный список
    private val dataSetFiltered: MutableList<String> = mutableListOf() // Отображаемый список

    private var filterableText = ""

    private val filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterableText = ""
            dataSetFiltered.clear()
            if (constraint != null) {
                for (item in dataSet) {
                    val name = constraint.toString().toLowerCase()
                    if (item.toLowerCase().startsWith(name)) {
                        filterableText = name
                        dataSetFiltered.add(item)
                    }
                }
                filterResults.values = dataSetFiltered
                filterResults.count = dataSetFiltered.size
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filteredList = results?.values as? List<String>
            clear()
            if (filteredList != null && filteredList.isNotEmpty()) {
                addAll(filteredList)
            }
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val currentView: View
        if(convertView == null) {
            currentView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_completion, parent, false)
            viewHolder = ViewHolder()
            viewHolder.textView = currentView.findViewById(R.id.item_title)
            currentView.tag = viewHolder
        } else {
            currentView = convertView
            viewHolder = currentView.tag as ViewHolder
        }

        val stringBuilder = SpannableStringBuilder(getItem(position))
        stringBuilder.setSpan(
            ForegroundColorSpan(color),
            0, filterableText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        viewHolder.bind(stringBuilder)
        return currentView
    }

    override fun getFilter(): Filter = filter

    class ViewHolder {
        lateinit var textView: TextView

        fun bind(spannable: Spannable) {
            textView.text = spannable
        }
    }
}