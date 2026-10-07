/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.data.model

import android.content.Context
import org.json.JSONArray
import java.util.concurrent.ConcurrentHashMap

/**
 * Keyword lists used by the editor's code completion (shown as suggestions while typing).
 *
 * Every language has its own file in `assets/keyword/<name>.json` containing a JSON array of
 * strings, so keywords can be edited or added without touching any code.
 */
internal object LanguageKeywords {

    private const val ASSET_DIR = "keyword"

    private val files = mapOf(
        LanguageScope.BAT to "bat",
        LanguageScope.C to "c",
        LanguageScope.CLOJURE to "clojure",
        LanguageScope.CPP to "cpp",
        LanguageScope.CSHARP to "csharp",
        LanguageScope.CSS to "css",
        LanguageScope.DART to "dart",
        LanguageScope.DOCKER to "dockerfile",
        LanguageScope.FORTRAN to "fortran",
        LanguageScope.FSHARP to "fsharp",
        LanguageScope.GO to "go",
        LanguageScope.GROOVY to "groovy",
        LanguageScope.HTML to "html",
        LanguageScope.JAVA to "java",
        LanguageScope.JAVASCRIPT to "javascript",
        LanguageScope.JULIA to "julia",
        LanguageScope.KOTLIN to "kotlin",
        LanguageScope.LATEX to "latex",
        LanguageScope.LISP to "lisp",
        LanguageScope.LUA to "lua",
        LanguageScope.MAKE to "makefile",
        LanguageScope.PERL to "perl",
        LanguageScope.PHP to "php",
        LanguageScope.PYTHON to "python",
        LanguageScope.RUBY to "ruby",
        LanguageScope.RUST to "rust",
        LanguageScope.SHELL to "shellscript",
        LanguageScope.SMALI to "smali",
        LanguageScope.SQL to "sql",
        LanguageScope.TYPESCRIPT to "typescript",
        LanguageScope.VISUALBASIC to "vb",
        LanguageScope.ZIG to "zig",
        LanguageScope.VUE to "vue",
        LanguageScope.SWIFT to "swift",
        LanguageScope.R to "r",
        LanguageScope.POWERSHELL to "powershell",
        LanguageScope.OBJC to "objc",
        LanguageScope.LESS to "less",
        LanguageScope.SCSS to "scss",
        LanguageScope.COFFEESCRIPT to "coffeescript",
        LanguageScope.PUG to "pug",
        LanguageScope.HANDLEBARS to "handlebars",
    )

    private val cache = ConcurrentHashMap<String, Array<String>>()

    fun forScope(context: Context, scope: String): Array<String>? {
        val name = files[scope] ?: return null
        cache[name]?.let { return it }
        return try {
            val json = context.assets.open("$ASSET_DIR/$name.json")
                .bufferedReader()
                .use { it.readText() }
            val array = JSONArray(json)
            val keywords = Array(array.length()) { array.getString(it) }
            cache[name] = keywords
            keywords
        } catch (e: Exception) {
            null
        }
    }
}