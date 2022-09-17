/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.viewmodel

import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class ExplorerIntent {

    data class SearchFiles(val query: String) : ExplorerIntent()
    data class SelectFiles(val selection: List<FileModel>) : ExplorerIntent()
    data class SelectTab(val position: Int) : ExplorerIntent()
    data class SelectFilesystem(val position: Int) : ExplorerIntent()
    object Refresh : ExplorerIntent()

    object Cut : ExplorerIntent()
    object Copy : ExplorerIntent()
    object Create : ExplorerIntent()
    object Rename : ExplorerIntent()
    object Delete : ExplorerIntent()
    object SelectAll : ExplorerIntent()
    object Properties : ExplorerIntent()
    object CopyPath : ExplorerIntent()
    object Compress : ExplorerIntent()

    data class OpenFolder(val fileModel: FileModel? = null) : ExplorerIntent()
    data class OpenFileWith(val fileModel: FileModel? = null) : ExplorerIntent()
    data class OpenFile(val fileModel: FileModel) : ExplorerIntent()
    data class CreateFile(val fileName: String, val directory: Boolean) : ExplorerIntent()
    data class RenameFile(val fileName: String) : ExplorerIntent()
    data class CompressFile(val fileName: String) : ExplorerIntent()
    data class ExtractFile(val fileModel: FileModel) : ExplorerIntent()
    object DeleteFile : ExplorerIntent()
    object CutFile : ExplorerIntent()
    object CopyFile : ExplorerIntent()

    object ShowHidden : ExplorerIntent()
    object HideHidden : ExplorerIntent()
    object SortByName : ExplorerIntent()
    object SortBySize : ExplorerIntent()
    object SortByDate : ExplorerIntent()
}