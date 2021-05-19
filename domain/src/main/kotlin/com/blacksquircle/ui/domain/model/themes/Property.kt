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

package com.blacksquircle.ui.domain.model.themes

enum class Property(val key: String) {
    TEXT_COLOR("text_color"),
    BACKGROUND_COLOR("background_color"),
    GUTTER_COLOR("gutter_color"),
    GUTTER_DIVIDER_COLOR("gutter_divider_color"),
    GUTTER_CURRENT_LINE_NUMBER_COLOR("gutter_current_line_number_color"),
    GUTTER_TEXT_COLOR("gutter_text_color"),
    SELECTED_LINE_COLOR("selected_line_color"),
    SELECTION_COLOR("selection_color"),
    SUGGESTION_QUERY_COLOR("suggestion_query_color"),
    FIND_RESULT_BACKGROUND_COLOR("find_result_background_color"),
    DELIMITER_BACKGROUND_COLOR("delimiter_background_color"),
    NUMBER_COLOR("number_color"),
    OPERATOR_COLOR("operator_color"),
    KEYWORD_COLOR("keyword_color"),
    TYPE_COLOR("type_color"),
    LANG_CONST_COLOR("lang_const_color"),
    PREPROCESSOR_COLOR("preprocessor_color"),
    VARIABLE_COLOR("variable_color"),
    METHOD_COLOR("method_color"),
    STRING_COLOR("string_color"),
    COMMENT_COLOR("comment_color"),
    TAG_COLOR("tag_color"),
    TAG_NAME_COLOR("tag_name_color"),
    ATTR_NAME_COLOR("attr_name_color"),
    ATTR_VALUE_COLOR("attr_value_color"),
    ENTITY_REF_COLOR("entity_ref_color")
}