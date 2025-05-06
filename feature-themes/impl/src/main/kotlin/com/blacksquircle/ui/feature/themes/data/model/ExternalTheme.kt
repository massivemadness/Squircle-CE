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
internal data class ExternalTheme(
    @SerialName("type")
    val type: String? = null,
    @SerialName("colors")
    val colors: ExternalColors? = null,
)

@Serializable
internal data class ExternalColors(
    @SerialName("global.colorPrimary")
    val colorPrimary: String? = null,
    @SerialName("global.colorOutline")
    val colorOutline: String? = null,
    @SerialName("global.colorBackgroundPrimary")
    val colorBackgroundPrimary: String? = null,
    @SerialName("global.colorBackgroundSecondary")
    val colorBackgroundSecondary: String? = null,
    @SerialName("global.colorBackgroundTertiary")
    val colorBackgroundTertiary: String? = null,
    @SerialName("global.colorTextAndIconPrimary")
    val colorTextAndIconPrimary: String? = null,
    @SerialName("global.colorTextAndIconPrimaryInverse")
    val colorTextAndIconPrimaryInverse: String? = null,
    @SerialName("global.colorTextAndIconSecondary")
    val colorTextAndIconSecondary: String? = null,
    @SerialName("global.colorTextAndIconDisabled")
    val colorTextAndIconDisabled: String? = null,
    @SerialName("global.colorTextAndIconAdditional")
    val colorTextAndIconAdditional: String? = null,
    @SerialName("global.colorTextAndIconSuccess")
    val colorTextAndIconSuccess: String? = null,
    @SerialName("global.colorTextAndIconError")
    val colorTextAndIconError: String? = null,
)