/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.data.delegate

import com.brackeys.ui.language.actionscript.ActionScriptLanguage
import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.c.CLanguage
import com.brackeys.ui.language.csharp.CSharpLanguage
import com.brackeys.ui.language.javascript.JavaScriptLanguage
import com.brackeys.ui.language.json.JsonLanguage
import com.brackeys.ui.language.plaintext.PlainTextLanguage

object LanguageDelegate {

    fun provideLanguage(fileName: String): Language {
        return when {
            fileName.endsWith(JavaScriptLanguage.FILE_EXTENSION) -> JavaScriptLanguage()
            fileName.endsWith(JsonLanguage.FILE_EXTENSION) -> JsonLanguage()
            fileName.endsWith(ActionScriptLanguage.FILE_EXTENSION) -> ActionScriptLanguage()
            fileName.endsWith(CSharpLanguage.FILE_EXTENSION) -> CSharpLanguage()
            fileName.endsWith(CLanguage.FILE_EXTENSION) -> CLanguage()
            else -> PlainTextLanguage()
        }
    }
}