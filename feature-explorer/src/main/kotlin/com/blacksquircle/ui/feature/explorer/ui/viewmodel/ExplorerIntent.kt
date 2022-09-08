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
    data class CreateFile(val fileName: String, val isFolder: Boolean) : ExplorerIntent()
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