package com.blacksquircle.ui.feature.explorer.ui.viewstate

import com.blacksquircle.ui.core.ui.viewstate.ViewState
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class ExplorerViewState : ViewState() {

    object Stub : ExplorerViewState()

    data class Breadcrumbs(
        val breadcrumbs: List<FileModel>,
        val selection: List<FileModel>,
    ) : ExplorerViewState()
}