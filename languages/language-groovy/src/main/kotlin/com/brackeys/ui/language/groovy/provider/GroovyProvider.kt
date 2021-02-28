package com.brackeys.ui.language.groovy.provider

import com.brackeys.ui.language.base.model.Suggestion
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.utils.WordsManager

class GroovyProvider private constructor() : SuggestionProvider {

    companion object {

        private var groovyProvider: GroovyProvider? = null

        fun getInstance(): GroovyProvider {
            return groovyProvider ?: GroovyProvider().also {
                groovyProvider = it
            }
        }
    }

    private val wordsManager = WordsManager()

    override fun getAll(): Set<Suggestion> {
        return wordsManager.getWords()
    }

    override fun processLine(lineNumber: Int, text: String) {
        wordsManager.processLine(lineNumber, text)
    }

    override fun deleteLine(lineNumber: Int) {
        wordsManager.deleteLine(lineNumber)
    }

    override fun clearLines() {
        wordsManager.clearLines()
    }
}