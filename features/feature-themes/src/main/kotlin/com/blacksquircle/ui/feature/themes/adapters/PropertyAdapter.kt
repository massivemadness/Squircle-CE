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

package com.blacksquircle.ui.feature.themes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blacksquircle.ui.domain.model.themes.Property
import com.blacksquircle.ui.domain.model.themes.PropertyItem
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.databinding.ItemPropertyBinding
import com.blacksquircle.ui.utils.adapters.OnItemClickListener

class PropertyAdapter(
    private val onItemClickListener: OnItemClickListener<PropertyItem>
) : ListAdapter<PropertyItem, PropertyAdapter.PropertyViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PropertyItem>() {
            override fun areItemsTheSame(oldItem: PropertyItem, newItem: PropertyItem): Boolean {
                return oldItem.propertyKey == newItem.propertyKey
            }
            override fun areContentsTheSame(oldItem: PropertyItem, newItem: PropertyItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        return PropertyViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PropertyViewHolder(
        private val binding: ItemPropertyBinding,
        private val onItemClickListener: OnItemClickListener<PropertyItem>
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup, onItemClickListener: OnItemClickListener<PropertyItem>): PropertyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPropertyBinding.inflate(inflater, parent, false)
                return PropertyViewHolder(binding, onItemClickListener)
            }
        }

        private lateinit var propertyItem: PropertyItem

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(propertyItem)
            }
        }

        fun bind(item: PropertyItem) {
            propertyItem = item
            binding.itemTitle.text = item.propertyKey.key
            binding.itemColor.drawable.setTint(item.propertyValue.toColorInt())

            val stringResId = when (item.propertyKey) {
                Property.TEXT_COLOR -> R.string.theme_property_text_color
                Property.BACKGROUND_COLOR -> R.string.theme_property_background_color
                Property.GUTTER_COLOR -> R.string.theme_property_gutter_color
                Property.GUTTER_DIVIDER_COLOR -> R.string.theme_property_gutter_divider_color
                Property.GUTTER_CURRENT_LINE_NUMBER_COLOR -> R.string.theme_property_gutter_current_line_number_color
                Property.GUTTER_TEXT_COLOR -> R.string.theme_property_gutter_text_color
                Property.SELECTED_LINE_COLOR -> R.string.theme_property_selected_line_color
                Property.SELECTION_COLOR -> R.string.theme_property_selection_color
                Property.SUGGESTION_QUERY_COLOR -> R.string.theme_property_suggestion_query_color
                Property.FIND_RESULT_BACKGROUND_COLOR -> R.string.theme_property_find_result_background_color
                Property.DELIMITER_BACKGROUND_COLOR -> R.string.theme_property_delimiter_background_color
                Property.NUMBER_COLOR -> R.string.theme_property_numbers_color
                Property.OPERATOR_COLOR -> R.string.theme_property_operators_color
                Property.KEYWORD_COLOR -> R.string.theme_property_keywords_color
                Property.TYPE_COLOR -> R.string.theme_property_types_color
                Property.LANG_CONST_COLOR -> R.string.theme_property_lang_const_color
                Property.PREPROCESSOR_COLOR -> R.string.theme_property_preprocessor_color
                Property.VARIABLE_COLOR -> R.string.theme_property_variables_color
                Property.METHOD_COLOR -> R.string.theme_property_methods_color
                Property.STRING_COLOR -> R.string.theme_property_strings_color
                Property.COMMENT_COLOR -> R.string.theme_property_comments_color
                Property.TAG_COLOR -> R.string.theme_property_tag_color
                Property.TAG_NAME_COLOR -> R.string.theme_property_tag_name_color
                Property.ATTR_NAME_COLOR -> R.string.theme_property_attr_name_color
                Property.ATTR_VALUE_COLOR -> R.string.theme_property_attr_value_color
                Property.ENTITY_REF_COLOR -> R.string.theme_property_entity_ref_color
            }
            binding.itemSubtitle.setText(stringResId)
        }
    }
}