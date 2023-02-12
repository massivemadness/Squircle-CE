/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.language.julia

import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.julia.parser.JuliaParser
import com.blacksquircle.ui.language.julia.provider.JuliaProvider
import com.blacksquircle.ui.language.julia.styler.JuliaStyler

class JuliaLanguage : Language {

    companion object {

        const val LANGUAGE_NAME = "julia"

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(".jl")
        }
    }

    override val languageName = LANGUAGE_NAME

    override fun getParser(): LanguageParser {
        /*try {
            // System.loadLibrary("julia-internal");
            // System.loadLibrary("julia");
            // System.loadLibrary("rustc_driver");  // err
        } catch (ex: Exception){
            ex.printStackTrace();
        }*/

        return JuliaParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return JuliaProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return JuliaStyler.getInstance()
    }
}