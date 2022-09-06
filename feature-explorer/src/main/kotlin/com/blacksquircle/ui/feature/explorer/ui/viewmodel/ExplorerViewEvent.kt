package com.blacksquircle.ui.feature.explorer.ui.viewmodel

import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.filesystem.base.model.FileModel

sealed class ExplorerViewEvent : ViewEvent() {

    data class OpenFile(val fileModel: FileModel) : ExplorerViewEvent()
    data class OpenFileWith(val fileModel: FileModel) : ExplorerViewEvent()
    data class CopyPath(val fileModel: FileModel) : ExplorerViewEvent()
    object SelectAll : ExplorerViewEvent()
}