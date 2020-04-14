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

import com.lightteam.modpeide.domain.feature.scheme.ColorScheme

data class Obsidian(
    override val textColor: String = "#E0E2E4",
    override val backgroundColor: String = "#2A3134",
    override val gutterColor: String = "#2A3134",
    override val gutterDividerColor: String = "#67777B",
    override val gutterCurrentLineNumberColor: String = "#E0E0E0",
    override val gutterTextColor: String = "#859599",
    override val selectedLineColor: String = "#31393C",
    override val selectionColor: String = "#616161",
    override val filterableColor: String = "#9EC56F",

    override val searchBgColor: String = "#838177",
    override val bracketBgColor: String = "#616161",

    override val numbersColor: String = "#F8CE4E",
    override val symbolsColor: String = "#E7E2BC",
    override val bracketsColor: String = "#E7E2BC",
    override val keywordsColor: String = "#9EC56F",
    override val methodsColor: String = "#E7E2BC",
    override val stringsColor: String = "#DE7C2E",
    override val commentsColor: String = "#808C92"
) : ColorScheme