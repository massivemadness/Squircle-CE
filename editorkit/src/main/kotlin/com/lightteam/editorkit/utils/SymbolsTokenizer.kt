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

package com.lightteam.editorkit.utils

import android.widget.MultiAutoCompleteTextView

class SymbolsTokenizer : MultiAutoCompleteTextView.Tokenizer {

    companion object {
        private const val TOKEN = "!@#$%^&*()_+-={}|[]:;'<>/<.? \r\n\t"
    }

    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor
        while (i > 0 && !TOKEN.contains(text[i - 1])) {
            i--
        }
        while (i < cursor && text[i] == ' ') {
            i++
        }
        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        val length = text.length
        var i = cursor
        while (i < length) {
            if (TOKEN.contains(text[i - 1])) {
                return i
            } else {
                i++
            }
        }
        return length
    }

    override fun terminateToken(text: CharSequence): CharSequence {
        var i = text.length
        while (i > 0 && text[i - 1] == ' ') {
            i--
        }
        return text
    }
}