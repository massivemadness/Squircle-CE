/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.filesystem.base.model

/**
 * @author Dmitry Rubtsov
 */
enum class LineBreak(private val linebreak: String) {
    CR("\r"),
    LF("\n"),
    CRLF("\r\n");

    companion object {

        /**
         * Эти значения хранятся в SharedPreferences.
         */
        private const val CODE_CR = "1"
        private const val CODE_LF = "2"
        private const val CODE_CRLF = "3"

        private val cr = "\\r".toRegex()
        private val lf = "\\n".toRegex()
        private val crlf = "\\r\\n".toRegex()

        fun find(value: String): LineBreak {
            val linebreak = when (value) {
                CODE_CR -> "\r"
                CODE_LF -> "\n"
                CODE_CRLF -> "\r\n"
                else -> throw IllegalArgumentException("No linebreak found")
            }
            return checkNotNull(values().find { it.linebreak == linebreak })
        }
    }

    /**
     * Заменяет все EOL-разделители в [text] на выбранный [linebreak].
     */
    operator fun invoke(text: String): String {
        return when (this) {
            CR -> text.replace(lf, linebreak).replace(crlf, linebreak)
            LF -> text.replace(cr, linebreak).replace(crlf, linebreak)
            CRLF -> text.replace(cr, linebreak).replace(lf, linebreak)
        }
    }
}