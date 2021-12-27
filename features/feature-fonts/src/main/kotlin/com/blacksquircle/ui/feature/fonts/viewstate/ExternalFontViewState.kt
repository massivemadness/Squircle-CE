package com.blacksquircle.ui.feature.fonts.viewstate

import com.blacksquircle.ui.core.viewstate.ViewState

sealed class ExternalFontViewState : ViewState() {
    object Valid : ExternalFontViewState()
    object Invalid : ExternalFontViewState()
}