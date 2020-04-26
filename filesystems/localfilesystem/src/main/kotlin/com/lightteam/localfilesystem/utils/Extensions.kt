package com.lightteam.localfilesystem.utils

import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun File.size(): Long {
    if (isDirectory) {
        var length = 0L
        for (child in listFiles()!!) {
            length += child.size()
        }
        return length
    }
    return length()
}


fun Long.formatAsDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy EEE HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.formatAsSize(): String {
    if (this <= 0)
        return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return (DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))
            + " " + units[digitGroups])
}