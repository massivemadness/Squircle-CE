/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.filesystem.base.model

import java.io.File

data class FileModel(
    val fileUri: String,
    val filesystemUuid: String,
    val size: Long = 0L,
    val lastModified: Long = 0L,
    val isDirectory: Boolean = false,
    @Permission val permission: Int = Permission.EMPTY,
) {
    val scheme: String
        get() = fileUri.substringBefore("://")
    val path: String
        get() = fileUri.substringAfterLast("://").ifEmpty(File::separator)
    val name: String
        get() = path.substringAfterLast(File.separator)
    val extension: String
        get() = "." + name.substringAfterLast(".")
    val type: FileType
        get() = when (extension) {
            in TEXT -> FileType.TEXT
            in ARCHIVE -> FileType.ARCHIVE
            in IMAGE -> FileType.IMAGE
            in AUDIO -> FileType.AUDIO
            in VIDEO -> FileType.VIDEO
            in DOCUMENT -> FileType.DOCUMENT
            in EBOOK -> FileType.EBOOK
            in APPLICATION -> FileType.APPLICATION
            else -> FileType.DEFAULT
        }

    companion object {

        val TEXT = arrayOf(
            ".txt", ".js", ".json", ".java", ".kt", ".kts", ".md", ".lua",
            ".rb", ".as", ".cs", ".c", ".cpp", ".h", ".hpp", ".lisp", ".lsp",
            ".cl", ".l", ".py", ".pyw", ".pyi", ".vb", ".bas", ".cls", ".sql",
            ".sqlite", ".sqlite2", ".sqlite3", ".htm", ".html", ".xhtml", ".xht",
            ".xaml", ".xdf", ".xmpp", ".xml", ".sh", ".ksh", ".bsh", ".csh",
            ".tcsh", ".zsh", ".bash", ".groovy", ".gvy", ".gy", ".gsh", ".php",
            ".php3", ".php4", ".php5", ".phps", ".phtml", ".ts", ".ino", ".log",
            ".mjs", ".cjs", ".jl", ".yaml", ".yml", ".toml", ".gradle", ".mts",
            ".cts", ".smali", ".css", ".scss", ".go", ".tex", ".f77", ".f90", ".f95",
            ".f03", ".f08", ".f18", ".f", ".fpp", ".for", ".ftn", ".pf", ".env",
            ".rs", ".jsx", ".tsx", ".ini", ".properties", ".Dockerfile", ".Makefile",
            ".editorconfig",
        )
        val ARCHIVE = arrayOf(
            ".zip", ".jar", ".rar", ".7z", ".tar", ".gz", ".tgz",
            ".zipx", ".gtar", "xtar", ".z", ".xz", ".bz", ".bz2",
            ".zst", ".lzh", ".lzma", ".arj",
        )
        val IMAGE = arrayOf(
            ".png", ".jpg", ".jpeg", ".gif", ".webp", ".bmp", ".ico",
        )
        val AUDIO = arrayOf(
            ".mp2", ".mp3", ".ogg", ".wma", ".aac", ".wav", ".flac",
            ".amr", ".m4a", ".m4b", ".m4p", ".mid", ".midi", ".mpga",
            ".m3u",
        )
        val VIDEO = arrayOf(
            ".3gp", ".mp4", ".avi", ".wmv", ".mkv", ".mpe", ".mpg",
            ".mpeg", ".asf", ".m4v", ".mov", ".rmvb", ".m4u", ".m3u8",
        )
        val DOCUMENT = arrayOf(
            ".rtf", ".doc", ".docx", ".ppt", ".pptx", ".pps", ".ppsx",
            ".xls", ".xlsx", ".csv", ".wps", ".pdf",
        )
        val EBOOK = arrayOf(
            ".epub", ".umb", ".chm", ".ceb", ".pdg", ".caj",
        )
        val APPLICATION = arrayOf(
            ".apk", ".aab",
        )
    }
}