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

data class Monokai(
    override val textColor: String = "#F8F8F8",
    override val backgroundColor: String = "#272823",
    override val gutterColor: String = "#272823",
    override val gutterDividerColor: String = "#5B5A4F",
    override val gutterCurrentLineNumberColor: String = "#C8BBAC",
    override val gutterTextColor: String = "#5B5A4F",
    override val selectedLineColor: String = "#34352D",
    override val selectionColor: String = "#666666",
    override val filterableColor: String = "#7CE0F3",

    override val searchBgColor: String = "#5F5E5A",
    override val bracketBgColor: String = "#5F5E5A",

    override val numberColor: String = "#BB8FF8",
    override val operatorColor: String = "#F8F8F2",
    override val bracketColor: String = "#E8E2B7",
    override val keywordColor: String = "#EB347E",
    override val methodColor: String = "#B6E951",
    override val stringColor: String = "#EBE48C",
    override val commentColor: String = "#89826D"
) : ColorScheme