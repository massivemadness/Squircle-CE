/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.test.logger

import android.util.Log
import timber.log.Timber
import java.util.regex.Pattern

class ConsoleTree : Timber.DebugTree() {

    private val anonymousClass = Pattern.compile("""(\$\d+)+$""")

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val className = tag?.substringBefore('$', tag)
        val level = when (priority) {
            Log.VERBOSE -> 'V'
            Log.DEBUG -> 'D'
            Log.INFO -> 'I'
            Log.WARN -> 'W'
            Log.ERROR -> 'E'
            Log.ASSERT -> 'A'
            else -> '?'
        }
        println("$level/$className: $message")
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        val matcher = anonymousClass.matcher(element.className)
        val tag = when {
            matcher.find() -> matcher.replaceAll("")
            else -> element.className
        }
        return tag.substringAfterLast('.')
    }
}