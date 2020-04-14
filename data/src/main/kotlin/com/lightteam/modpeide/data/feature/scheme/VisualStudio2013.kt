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

package com.lightteam.modpeide.data.feature.scheme

import com.lightteam.language.scheme.ColorScheme

data class VisualStudio2013(
    override val textColor: String = "#C8C8C8",
    override val backgroundColor: String = "#232323",
    override val gutterColor: String = "#2C2C2C",
    override val gutterDividerColor: String = "#555555",
    override val gutterCurrentLineNumberColor: String = "#FFFFFF",
    override val gutterTextColor: String = "#C6C8C6",
    override val selectedLineColor: String = "#141414",
    override val selectionColor: String = "#454464",
    override val filterableColor: String = "#4F98F7",

    override val searchBgColor: String = "#1C3D6B",
    override val bracketBgColor: String = "#616161",

    override val numbersColor: String = "#BACDAB",
    override val symbolsColor: String = "#DCDCDC",
    override val bracketsColor: String = "#FFFFFF",
    override val keywordsColor: String = "#669BD1",
    override val methodsColor: String = "#71C6B1",
    override val stringsColor: String = "#CE9F89",
    override val commentsColor: String = "#6BA455"
) : ColorScheme