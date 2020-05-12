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

package com.lightteam.modpeide.data.feature.scheme.internal

import androidx.annotation.ColorInt

data class ColorScheme(
    @ColorInt
    val textColor: Int,
    @ColorInt
    val backgroundColor: Int,
    @ColorInt
    val gutterColor: Int,
    @ColorInt
    val gutterDividerColor: Int,
    @ColorInt
    val gutterCurrentLineNumberColor: Int,
    @ColorInt
    val gutterTextColor: Int,
    @ColorInt
    val selectedLineColor: Int,
    @ColorInt
    val selectionColor: Int,
    @ColorInt
    val suggestionQueryColor: Int,
    @ColorInt
    val findResultBackgroundColor: Int,
    @ColorInt
    val delimiterBackgroundColor: Int,
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
    val methodColor: Int,
    @ColorInt
    val stringColor: Int,
    @ColorInt
    val commentColor: Int
)