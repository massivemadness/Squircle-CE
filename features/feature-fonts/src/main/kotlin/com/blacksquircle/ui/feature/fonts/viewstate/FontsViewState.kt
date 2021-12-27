package com.blacksquircle.ui.feature.fonts.viewstate

import com.blacksquircle.ui.core.viewstate.ViewState
import com.blacksquircle.ui.domain.model.fonts.FontModel

sealed class FontsViewState : ViewState() {

    abstract val query: String

    data class Empty(
        override val query: String,
    ) : FontsViewState()

    data class Data(
        override val query: String,
        val fonts: List<FontModel>,
    ) : FontsViewState()
}