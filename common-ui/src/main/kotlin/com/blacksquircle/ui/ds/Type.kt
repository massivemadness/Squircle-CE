/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.ds

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val LocalTypography = staticCompositionLocalOf { Typography() }

@Immutable
class Typography internal constructor(
    val text12Regular: TextStyle = Default.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    val text14Regular: TextStyle = Default.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    val text14Medium: TextStyle = Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    val text14Bold: TextStyle = Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),
    val text16Regular: TextStyle = Default.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    val text16Medium: TextStyle = Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    val text16Bold: TextStyle = Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    val text18Regular: TextStyle = Default.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
    ),
    val text18Medium: TextStyle = Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),
    val text18Bold: TextStyle = Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),
    val text20Regular: TextStyle = Default.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    val text20Medium: TextStyle = Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),
    val header20Bold: TextStyle = Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
) {

    companion object {
        val Default = TextStyle.Default
    }
}