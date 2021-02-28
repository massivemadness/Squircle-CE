package com.brackeys.ui.language.groovy.parser

import com.brackeys.ui.language.base.exception.ParseException
import com.brackeys.ui.language.base.model.ParseResult
import com.brackeys.ui.language.base.parser.LanguageParser

class GroovyParser private constructor() : LanguageParser {

    companion object {

        private var groovyParser: GroovyParser? = null

        fun getInstance(): GroovyParser {
            return groovyParser ?: GroovyParser().also {
                groovyParser = it
            }
        }
    }

    override fun execute(name: String, source: String): ParseResult {
        // TODO Implement parser
        val parseException = ParseException("Unable to parse unsupported language", 0, 0)
        return ParseResult(parseException)
    }
}