package com.blacksquircle.ui.feature.explorer.ui.viewstate

import com.blacksquircle.ui.core.ui.viewstate.ViewState
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class DirectoryViewState : ViewState() {

    object Restricted : DirectoryViewState()
    object Loading : DirectoryViewState()
    object Empty : DirectoryViewState()
    object Stub : DirectoryViewState()

    data class Files(val data: List<FileModel>) : DirectoryViewState()
}