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

package com.blacksquircle.ui.language.base.model

import androidx.annotation.ColorInt

data class SyntaxScheme(
    @ColorInt
    val numberColor: Int,
    @ColorInt
    val operatorColor: Int,
    @ColorInt
    val keywordColor: Int,
    @ColorInt
    val typeColor: Int,
    @ColorInt
    val langConstColor: Int,
    @ColorInt
    val preprocessorColor: Int,
    @ColorInt
    val variableColor: Int,
    @ColorInt
    val methodColor: Int,
    @ColorInt
    val stringColor: Int,
    @ColorInt
    val commentColor: Int,
    @ColorInt
    val tagColor: Int,
    @ColorInt
    val tagNameColor: Int,
    @ColorInt
    val attrNameColor: Int,
    @ColorInt
    val attrValueColor: Int,
    @ColorInt
    val entityRefColor: Int
)