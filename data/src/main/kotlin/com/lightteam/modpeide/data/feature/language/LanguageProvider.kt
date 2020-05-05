/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.feature.language

import com.lightteam.javascript.language.JavaScriptLanguage
import com.lightteam.language.language.Language
import com.lightteam.unknown.language.UnknownLanguage

object LanguageProvider {

    fun provideLanguage(fileName: String): Language {
        return when {
            fileName.endsWith(JavaScriptLanguage.FILE_EXTENSION) -> JavaScriptLanguage()
            else -> UnknownLanguage()
        }
    }
}