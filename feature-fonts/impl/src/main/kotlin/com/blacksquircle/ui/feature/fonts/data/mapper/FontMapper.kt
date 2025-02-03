/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.fonts.data.mapper

import com.blacksquircle.ui.core.storage.database.entity.font.FontEntity
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel

internal object FontMapper {

    fun toModel(fontEntity: FontEntity): FontModel {
        return FontModel(
            uuid = fontEntity.fontUuid,
            name = fontEntity.fontName,
            path = fontEntity.fontPath,
            isExternal = true,
        )
    }

    fun toEntity(fontModel: FontModel): FontEntity {
        return FontEntity(
            fontUuid = fontModel.uuid,
            fontName = fontModel.name,
            fontPath = fontModel.path,
            supportLigatures = false,
        )
    }
}