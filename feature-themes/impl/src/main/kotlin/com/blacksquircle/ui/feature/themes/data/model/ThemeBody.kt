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

package com.blacksquircle.ui.feature.themes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ThemeBody(
    @SerialName("type")
    val type: String?,
    @SerialName("colors")
    val colors: ThemeColors?,
)

@Serializable
internal data class ThemeColors(
    @SerialName("global.colorPrimary")
    val colorPrimary: String?,
    @SerialName("global.colorOutline")
    val colorOutline: String?,
    @SerialName("global.colorSuccess")
    val colorSuccess: String?,
    @SerialName("global.colorError")
    val colorError: String?,
    @SerialName("global.colorBackgroundPrimary")
    val colorBackgroundPrimary: String?,
    @SerialName("global.colorBackgroundSecondary")
    val colorBackgroundSecondary: String?,
    @SerialName("global.colorBackgroundTertiary")
    val colorBackgroundTertiary: String?,
    @SerialName("global.colorTextAndIconPrimary")
    val colorTextAndIconPrimary: String?,
    @SerialName("global.colorTextAndIconPrimaryInverse")
    val colorTextAndIconPrimaryInverse: String?,
    @SerialName("global.colorTextAndIconSecondary")
    val colorTextAndIconSecondary: String?,
    @SerialName("global.colorTextAndIconDisabled")
    val colorTextAndIconDisabled: String?,
    @SerialName("global.colorTextAndIconAdditional")
    val colorTextAndIconAdditional: String?,
)