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

package com.blacksquircle.ui.editorkit.model

import androidx.annotation.ColorInt
import com.blacksquircle.ui.language.base.model.SyntaxScheme

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
    val syntaxScheme: SyntaxScheme
)