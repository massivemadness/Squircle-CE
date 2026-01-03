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

package com.blacksquircle.ui.feature.editor.data.provider

import com.blacksquircle.ui.feature.editor.api.provider.FileIconProvider
import com.blacksquircle.ui.feature.editor.data.model.FileAssociation
import com.blacksquircle.ui.feature.editor.data.model.LanguageScope
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.ds.R as UiR

internal class FileIconProviderImpl : FileIconProvider {

    override fun fileIcon(fileModel: FileModel): Int {
        val language = FileAssociation.guessLanguage(fileModel.extension)
        return when (language) {
            LanguageScope.BAT -> UiR.drawable.ic_file_document
            LanguageScope.C -> UiR.drawable.ic_language_c
            LanguageScope.CLOJURE -> UiR.drawable.ic_file_code
            LanguageScope.CPP -> UiR.drawable.ic_language_cpp
            LanguageScope.CSHARP -> UiR.drawable.ic_language_csharp
            LanguageScope.CSS -> UiR.drawable.ic_language_css
            LanguageScope.DART -> UiR.drawable.ic_file_code
            LanguageScope.DOCKER -> UiR.drawable.ic_language_docker
            LanguageScope.FORTRAN -> UiR.drawable.ic_language_fortran
            LanguageScope.FSHARP -> UiR.drawable.ic_file_code
            LanguageScope.GO -> UiR.drawable.ic_language_go
            LanguageScope.GROOVY -> UiR.drawable.ic_file_code
            LanguageScope.HTML -> UiR.drawable.ic_language_html
            LanguageScope.INI -> UiR.drawable.ic_language_ini
            LanguageScope.JAVA -> UiR.drawable.ic_language_java
            LanguageScope.JAVASCRIPT -> UiR.drawable.ic_language_javascript
            LanguageScope.JSON -> UiR.drawable.ic_language_json
            LanguageScope.JULIA -> UiR.drawable.ic_file_code
            LanguageScope.KOTLIN -> UiR.drawable.ic_language_kotlin
            LanguageScope.LATEX -> UiR.drawable.ic_file_code
            LanguageScope.LISP -> UiR.drawable.ic_file_code
            LanguageScope.LUA -> UiR.drawable.ic_language_lua
            LanguageScope.MAKE -> UiR.drawable.ic_file_code
            LanguageScope.MARKDOWN -> UiR.drawable.ic_language_markdown
            LanguageScope.PERL -> UiR.drawable.ic_file_code
            LanguageScope.PHP -> UiR.drawable.ic_language_php
            LanguageScope.PYTHON -> UiR.drawable.ic_language_python
            LanguageScope.RUBY -> UiR.drawable.ic_language_ruby
            LanguageScope.RUST -> UiR.drawable.ic_language_rust
            LanguageScope.SHELL -> UiR.drawable.ic_language_shell
            LanguageScope.SMALI -> UiR.drawable.ic_file_code
            LanguageScope.SQL -> UiR.drawable.ic_language_sql
            LanguageScope.TEXT -> UiR.drawable.ic_file_document
            LanguageScope.TOML -> UiR.drawable.ic_file_document
            LanguageScope.TYPESCRIPT -> UiR.drawable.ic_language_typescript
            LanguageScope.VISUALBASIC -> UiR.drawable.ic_file_code
            LanguageScope.XML -> UiR.drawable.ic_language_xml
            LanguageScope.YAML -> UiR.drawable.ic_file_code
            LanguageScope.ZIG -> UiR.drawable.ic_file_code
            LanguageScope.VUE -> UiR.drawable.ic_file_code
            else -> -1
        }
    }
}