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

package com.blacksquircle.ui.filesystem.base.model

/**
 * @author Dmitrii Rubtsov
 */
enum class LineBreak(val linebreak: String) {
    CR("\r"),
    LF("\n"),
    CRLF("\r\n");

    /**
     * Заменяет все EOL-разделители в [text] на выбранный [linebreak].
     */
    fun replace(text: String): String {
        return when (this) {
            CR -> text.replace("($VALUE_CRLF|$VALUE_LF)".toRegex(), linebreak)
            LF -> text.replace("($VALUE_CRLF|$VALUE_CR)".toRegex(), linebreak)
            CRLF -> text.replace("($VALUE_CR|$VALUE_LF)".toRegex(), linebreak)
        }
    }

    companion object {

        /**
         * Эти значения хранятся в SharedPreferences
         */
        private const val ORD_CR = "1"
        private const val ORD_LF = "2"
        private const val ORD_CRLF = "3"

        private const val VALUE_CR = "\\r"
        private const val VALUE_LF = "\\n"
        private const val VALUE_CRLF = "\\r\\n"

        fun of(value: String): LineBreak {
            val linebreak = when (value) {
                ORD_CR -> "\r"
                ORD_LF -> "\n"
                ORD_CRLF -> "\r\n"
                else -> throw IllegalArgumentException("No linebreak found")
            }
            return checkNotNull(values().find { it.linebreak == linebreak })
        }
    }
}