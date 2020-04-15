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

package com.lightteam.language.styler

import android.os.AsyncTask
import android.util.Log
import com.lightteam.language.scheme.ColorScheme
import com.lightteam.language.styler.span.StyleSpan
import com.lightteam.language.styler.span.SyntaxHighlightSpan

abstract class LanguageStyler {

    companion object {
        private const val TAG = "LanguageStyler"
    }

    lateinit var syntaxHighlightSpans: MutableList<SyntaxHighlightSpan>

    private lateinit var syntaxStyleable: Styleable
    private var task: StylingTask? = null
    private var cancelled = false

    protected lateinit var sourceCode: String
    protected lateinit var colorScheme: ColorScheme

    protected var parseStart = 0
    protected var parseEnd = 0

    abstract fun parse()

    fun runStyler(styleable: Styleable, source: String, scheme: ColorScheme) {
        task?.cancelTask()
        task = StylingTask()
        syntaxStyleable = styleable
        sourceCode = source
        colorScheme = scheme
        parseStart = 0
        parseEnd = source.length
        try {
            task?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    fun cancelStyler() {
        cancelled = true
        task?.cancelTask()
        task = null
    }

    fun addSpan(styleSpan: StyleSpan, start: Int, end: Int) {
        if (end > start && end >= 0 && start >= 0
            && start <= sourceCode.length && end <= sourceCode.length) {
            syntaxHighlightSpans.add(SyntaxHighlightSpan(styleSpan, start, end))
        }
    }

    private inner class StylingTask : AsyncTask<Void, Void, Void>() {

        private var isError = false

        override fun doInBackground(vararg p0: Void?): Void? {
            syntaxHighlightSpans = mutableListOf()
            try {
                parse()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                isError = true
            }
            return null
        }

        override fun onPostExecute(voidR: Void?) {
            if (!isError && !isCancelled && !cancelled) {
                syntaxStyleable.setSpans(syntaxHighlightSpans)
            }
        }

        fun cancelTask(): Boolean {
            return cancel(true)
        }
    }
}
