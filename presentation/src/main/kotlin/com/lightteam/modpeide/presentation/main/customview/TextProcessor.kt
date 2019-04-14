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

package com.lightteam.modpeide.presentation.main.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import com.lightteam.modpeide.R

class TextProcessor(context: Context, attrs: AttributeSet) : AppCompatMultiAutoCompleteTextView(context, attrs) {

    data class Configuration(
        var fontSize: Float = 14f,
        var fontType: Typeface = Typeface.MONOSPACE
    )

    private val typedArray = context.theme.obtainStyledAttributes(
        attrs, R.styleable.TextProcessor, 0, 0
    )

    private val numbersColor: Int = typedArray.getColor(R.styleable.TextProcessor_numbersColor, Color.WHITE)
    private val symbolsColor: Int = typedArray.getColor(R.styleable.TextProcessor_symbolsColor, Color.WHITE)
    private val bracketsColor: Int = typedArray.getColor(R.styleable.TextProcessor_bracketsColor, Color.WHITE)
    private val keywordsColor: Int = typedArray.getColor(R.styleable.TextProcessor_keywordsColor, Color.WHITE)
    private val methodsColor: Int = typedArray.getColor(R.styleable.TextProcessor_methodsColor, Color.WHITE)
    private val stringsColor: Int = typedArray.getColor(R.styleable.TextProcessor_stringsColor, Color.WHITE)
    private val commentsColor: Int = typedArray.getColor(R.styleable.TextProcessor_commentsColor, Color.WHITE)

    private var configuration: Configuration = Configuration()

    init {
        configure(configuration)
    }

    fun getConfiguration() = configuration
    fun setConfiguration(configuration: Configuration) = configure(configuration)

    private fun configure(configuration: Configuration) {
        this.configuration = configuration

        textSize = configuration.fontSize
        typeface = configuration.fontType
    }
}