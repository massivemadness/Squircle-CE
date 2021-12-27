package com.blacksquircle.ui.domain.providers.resources

interface StringProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}