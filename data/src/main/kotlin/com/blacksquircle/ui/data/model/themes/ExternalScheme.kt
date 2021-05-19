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

package com.blacksquircle.ui.data.model.themes

import com.google.gson.annotations.SerializedName

data class ExternalScheme(
    @SerializedName("text_color")
    val textColor: String?,
    @SerializedName("background_color")
    val backgroundColor: String?,
    @SerializedName("gutter_color")
    val gutterColor: String?,
    @SerializedName("gutter_divider_color")
    val gutterDividerColor: String?,
    @SerializedName("gutter_current_line_number_color")
    val gutterCurrentLineNumberColor: String?,
    @SerializedName("gutter_text_color")
    val gutterTextColor: String?,
    @SerializedName("selected_line_color")
    val selectedLineColor: String?,
    @SerializedName("selection_color")
    val selectionColor: String?,
    @SerializedName("suggestion_query_color")
    val suggestionQueryColor: String?,
    @SerializedName("find_result_background_color")
    val findResultBackgroundColor: String?,
    @SerializedName("delimiter_background_color")
    val delimiterBackgroundColor: String?,
    @SerializedName("number_color")
    val numberColor: String?,
    @SerializedName("operator_color")
    val operatorColor: String?,
    @SerializedName("keyword_color")
    val keywordColor: String?,
    @SerializedName("type_color")
    val typeColor: String?,
    @SerializedName("lang_const_color")
    val langConstColor: String?,
    @SerializedName("preprocessor_color")
    val preprocessorColor: String?,
    @SerializedName("variable_color")
    val variableColor: String?,
    @SerializedName("method_color")
    val methodColor: String?,
    @SerializedName("string_color")
    val stringColor: String?,
    @SerializedName("comment_color")
    val commentColor: String?,
    @SerializedName("tag_color")
    val tagColor: String?,
    @SerializedName("tag_name_color")
    val tagNameColor: String?,
    @SerializedName("attr_name_color")
    val attrNameColor: String?,
    @SerializedName("attr_value_color")
    val attrValueColor: String?,
    @SerializedName("entity_ref_color")
    val entityRefColor: String?
)