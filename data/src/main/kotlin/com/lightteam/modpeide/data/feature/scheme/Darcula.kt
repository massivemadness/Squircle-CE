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

data class Darcula(
    override val textColor: String = "#ABB7C5",
    override val backgroundColor: String = "#303030",
    override val gutterColor: String = "#313335",
    override val gutterDividerColor: String = "#555555",
    override val gutterCurrentLineNumberColor: String = "#A4A3A3",
    override val gutterTextColor: String = "#616366",
    override val selectedLineColor: String = "#3A3A3A",
    override val selectionColor: String = "#28427F",
    override val filterableColor: String = "#987DAC",

    override val searchBgColor: String = "#33654B",
    override val bracketBgColor: String = "#33654B",

    override val numberColor: String = "#6897BB",
    override val operatorColor: String = "#E8E2B7",
    override val bracketColor: String = "#E8E2B7",
    override val keywordColor: String = "#EC7600",
    override val typeColor: String = "#EC7600",
    override val langConstColor: String = "#EC7600",
    override val methodColor: String = "#FEC76C",
    override val stringColor: String = "#6E875A",
    override val commentColor: String = "#66747B"
) : ColorScheme