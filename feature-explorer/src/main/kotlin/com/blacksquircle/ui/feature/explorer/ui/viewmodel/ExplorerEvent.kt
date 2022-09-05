package com.blacksquircle.ui.feature.explorer.ui.viewmodel

import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class ExplorerEvent {

    data class ListFiles(val parent: FileModel? = null) : ExplorerEvent()
    data class SearchFiles(val query: String) : ExplorerEvent()
    data class SelectFiles(val selection: List<FileModel>) : ExplorerEvent()
    data class SelectTab(val position: Int) : ExplorerEvent()
    object Refresh : ExplorerEvent()

    object Cut : ExplorerEvent()
    object Copy : ExplorerEvent()
    object Paste : ExplorerEvent()
    object Create : ExplorerEvent()
    object Rename : ExplorerEvent()
    object Delete : ExplorerEvent()
    object SelectAll : ExplorerEvent()
    object Properties : ExplorerEvent()
    object CopyPath : ExplorerEvent()
    object Compress : ExplorerEvent()

    data class OpenFileAs(val fileModel: FileModel? = null) : ExplorerEvent()
    data class OpenFile(val fileModel: FileModel) : ExplorerEvent()
    data class CreateFile(val fileName: String, val isFolder: Boolean) : ExplorerEvent()
    data class RenameFile(val fileName: String) : ExplorerEvent()
    data class CompressFile(val fileName: String) : ExplorerEvent()
    data class ExtractFile(val fileModel: FileModel) : ExplorerEvent()
    object DeleteFile : ExplorerEvent()

    object ShowHidden : ExplorerEvent()
    object HideHidden : ExplorerEvent()
    object SortByName : ExplorerEvent()
    object SortBySize : ExplorerEvent()
    object SortByDate : ExplorerEvent()
}