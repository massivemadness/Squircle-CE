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

package com.blacksquircle.ui.feature.explorer.ui.mvi

import com.blacksquircle.ui.core.mvi.ViewIntent
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class ExplorerIntent : ViewIntent() {

    data class SearchFiles(val query: String) : ExplorerIntent()
    data class SelectFiles(val selection: List<FileModel>) : ExplorerIntent()
    data class SelectTab(val position: Int) : ExplorerIntent()
    data class SelectFilesystem(val filesystemUuid: String) : ExplorerIntent()
    data class Authenticate(val password: String) : ExplorerIntent()
    data object Refresh : ExplorerIntent()

    data object Cut : ExplorerIntent()
    data object Copy : ExplorerIntent()
    data object Create : ExplorerIntent()
    data object Rename : ExplorerIntent()
    data object Delete : ExplorerIntent()
    data object SelectAll : ExplorerIntent()
    data object UnselectAll : ExplorerIntent()
    data object Properties : ExplorerIntent()
    data object CopyPath : ExplorerIntent()
    data object Compress : ExplorerIntent()

    data class OpenFolder(val fileModel: FileModel? = null) : ExplorerIntent()
    data class OpenFileWith(val fileModel: FileModel? = null) : ExplorerIntent()
    data class OpenFile(val fileModel: FileModel) : ExplorerIntent()
    data class CreateFile(val fileName: String, val directory: Boolean) : ExplorerIntent()
    data class RenameFile(val fileName: String) : ExplorerIntent()
    data class CompressFile(val fileName: String) : ExplorerIntent()
    data class ExtractFile(val fileModel: FileModel) : ExplorerIntent()
    data object DeleteFile : ExplorerIntent()
    data object CutFile : ExplorerIntent()
    data object CopyFile : ExplorerIntent()

    data object ShowHidden : ExplorerIntent()
    data object HideHidden : ExplorerIntent()
    data object SortByName : ExplorerIntent()
    data object SortBySize : ExplorerIntent()
    data object SortByDate : ExplorerIntent()
}