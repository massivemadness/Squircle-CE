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

package com.lightteam.modpeide.data.converter

import com.lightteam.modpeide.database.entity.preset.PresetEntity
import com.lightteam.modpeide.domain.model.preset.PresetModel
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PresetConverterTest {

    @Test
    fun `convert PresetEntity to PresetModel`() {
        val presetEntity = PresetEntity(
            uuid = "id123",
            name = "Default",
            isExternal = false,
            keys = "{}[]()"
        )
        val presetModel = PresetModel(
            uuid = "id123",
            name = "Default",
            isExternal = false,
            keys = listOf("{", "}", "[", "]", "(", ")")
        )
        val convert = PresetConverter.toModel(presetEntity)

        assertEquals(presetModel.uuid, convert.uuid)
        assertEquals(presetModel.name, convert.name)
        assertEquals(presetModel.isExternal, convert.isExternal)
        assertEquals(presetModel.keys, convert.keys)
    }

    @Test
    fun `convert PresetModel to PresetEntity`() {
        val presetEntity = PresetEntity(
            uuid = "id123",
            name = "Default",
            isExternal = false,
            keys = "{}[]()"
        )
        val presetModel = PresetModel(
            uuid = "id123",
            name = "Default",
            isExternal = false,
            keys = listOf("{", "}", "[", "]", "(", ")")
        )
        val convert = PresetConverter.toEntity(presetModel)

        assertEquals(presetEntity.uuid, convert.uuid)
        assertEquals(presetEntity.name, convert.name)
        assertEquals(presetEntity.isExternal, convert.isExternal)
        assertEquals(presetEntity.keys, convert.keys)
    }
}