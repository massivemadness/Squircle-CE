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

package com.blacksquircle.ui.feature.editor.interactor

import android.net.Uri
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.feature.editor.data.interactor.EditorInteractorImpl
import com.blacksquircle.ui.filesystem.base.model.FileModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EditorInteractorImplTest {

    private val editorInteractor = EditorInteractorImpl()

    @Test
    fun `When openFile called Then send file to event bus`() = runTest {
        // Given
        val fileModel = mockk<FileModel>()

        // When
        editorInteractor.openFile(fileModel)

        // Then
        val event = EditorApiEvent.OpenFile(fileModel)
        assertEquals(event, editorInteractor.eventBus.first())
    }

    @Test
    fun `When openFileUri called Then send uri to event bus`() = runTest {
        // Given
        val fileUri = mockk<Uri>()

        // When
        editorInteractor.openFileUri(fileUri)

        // Then
        val event = EditorApiEvent.OpenFileUri(fileUri)
        assertEquals(event, editorInteractor.eventBus.first())
    }
}