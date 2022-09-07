package com.blacksquircle.ui.feature.explorer.ui.viewstate

import com.blacksquircle.ui.core.ui.viewstate.ViewState
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class DirectoryViewState : ViewState() {

    object Permission : DirectoryViewState()
    object Loading : DirectoryViewState()
    object Stub : DirectoryViewState()

    data class Files(
        val data: List<FileModel>,
    ) : DirectoryViewState()

    data class Error(
        val image: Int,
        val title: String,
        val subtitle: String,
    ) : DirectoryViewState()
}