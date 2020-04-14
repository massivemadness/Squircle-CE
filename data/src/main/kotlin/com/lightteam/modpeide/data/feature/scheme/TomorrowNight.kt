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

data class TomorrowNight(
    override val textColor: String = "#C6C8C6",
    override val backgroundColor: String = "#222426",
    override val gutterColor: String = "#222426",
    override val gutterDividerColor: String = "#4B4D51",
    override val gutterCurrentLineNumberColor: String = "#FFFFFF",
    override val gutterTextColor: String = "#C6C8C6",
    override val selectedLineColor: String = "#2D2F33",
    override val selectionColor: String = "#383B40",
    override val filterableColor: String = "#EAC780",

    override val searchBgColor: String = "#4B4E54",
    override val bracketBgColor: String = "#616161",

    override val numbersColor: String = "#D49668",
    override val symbolsColor: String = "#CFD1CF",
    override val bracketsColor: String = "#C6C8C6",
    override val keywordsColor: String = "#AD95B8",
    override val methodsColor: String = "#87A1BB",
    override val stringsColor: String = "#B7BC73",
    override val commentsColor: String = "#969896"
) : ColorScheme