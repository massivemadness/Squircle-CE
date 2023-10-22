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

package com.blacksquircle.ui.feature.changelog

import com.blacksquircle.ui.feature.changelog.data.converter.ReleaseConverter
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ReleaseConverterTest {

    @Test
    fun `When converting changelog Then check list size`() {
        // Given
        val testData = """
            <b>v2018.1.2, 11 Feb. 2018</b><br>
            • <b>Added:</b> Line №1<br>
            • <b>Fixed:</b> Line №2<br>
            • <b>Fixed:</b> Line №3<br>
            • Line №4<br>
            <br>
            <b>v2018.1.1, 28 Jan. 2018</b><br>
            • <b>Added:</b> Line №1<br>
            • <b>Added:</b> Line №2<br>
            • Line №3<br>
            <br>
            <b>v2018.1.0, 23 Jan. 2018</b><br>
            • Line №1<br>
            <br>
        """.trimIndent()

        // When
        val releaseList = ReleaseConverter.toReleaseModels(testData)

        // Then
        assertEquals(3, releaseList.size)
    }

    @Test
    fun `When converting changelog Then verify release info`() {
        // Given
        val testData = """
            <b>v2018.1.2, 11 Feb. 2018</b><br>
            • <b>Added:</b> Line №1<br>
            • <b>Fixed:</b> Line №2<br>
            • <b>Fixed:</b> Line №3<br>
            • Line №4<br>
            <br>
        """.trimIndent()

        // When
        val releaseModel = ReleaseConverter.toReleaseModels(testData).first()

        // Then
        assertEquals("v2018.1.2", releaseModel.versionName)
        assertEquals("11 Feb. 2018", releaseModel.releaseDate)
    }
}