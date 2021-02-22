package com.brackeys.ui.utils.extensions

fun <T> MutableList<T>.replaceList(collection: Collection<T>) {
    clear()
    addAll(collection)
}