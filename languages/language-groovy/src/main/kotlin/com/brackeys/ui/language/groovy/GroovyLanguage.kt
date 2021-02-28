package com.brackeys.ui.language.groovy

import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.base.parser.LanguageParser
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.styler.LanguageStyler
import com.brackeys.ui.language.base.utils.endsWith
import com.brackeys.ui.language.groovy.parser.GroovyParser
import com.brackeys.ui.language.groovy.provider.GroovyProvider
import com.brackeys.ui.language.groovy.styler.GroovyStyler

class GroovyLanguage : Language {

    companion object {

        private val FILE_EXTENSIONS = arrayOf(".groovy", ".gvy", ".gy", ".gsh")

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(FILE_EXTENSIONS)
        }
    }

    override fun getName(): String {
        return "groovy"
    }

    override fun getParser(): LanguageParser {
        return GroovyParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return GroovyProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return GroovyStyler.getInstance()
    }
}