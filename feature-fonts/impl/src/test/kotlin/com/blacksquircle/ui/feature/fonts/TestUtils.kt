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

package com.blacksquircle.ui.feature.fonts

import android.graphics.Typeface
import com.blacksquircle.ui.core.database.entity.font.FontEntity
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import io.mockk.mockk

internal fun createFontEntity(
    uuid: String = "1",
    name: String = "Custom Font"
): FontEntity {
    return FontEntity(
        fontUuid = uuid,
        fontName = name,
    )
}

internal fun createFontModel(
    uuid: String = "1",
    name: String = "Custom Font",
    typeface: Typeface = mockk(),
    isExternal: Boolean = true,
): FontModel {
    return FontModel(
        uuid = uuid,
        name = name,
        typeface = typeface,
        isExternal = isExternal,
    )
}