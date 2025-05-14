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

package com.blacksquircle.ui.feature.editor.data.interactor

import android.net.Uri
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.model.EditorApiEvent
import com.blacksquircle.ui.filesystem.base.model.FileModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

internal class EditorInteractorImpl : EditorInteractor {

    private val _eventBus = Channel<EditorApiEvent>(Channel.BUFFERED)
    override val eventBus: Flow<EditorApiEvent> = _eventBus.receiveAsFlow()

    override suspend fun openFile(fileModel: FileModel) {
        _eventBus.send(EditorApiEvent.OpenFile(fileModel))
    }

    override suspend fun openFileUri(fileUri: Uri) {
        _eventBus.send(EditorApiEvent.OpenFileUri(fileUri))
    }

    override suspend fun deleteFile(fileModel: FileModel) {
        _eventBus.send(EditorApiEvent.DeleteFile(fileModel))
    }
}