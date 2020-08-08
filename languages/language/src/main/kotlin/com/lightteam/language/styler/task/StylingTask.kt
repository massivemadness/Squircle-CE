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

package com.lightteam.language.styler.task

import android.os.AsyncTask
import android.util.Log
import com.lightteam.language.styler.span.SyntaxHighlightSpan

class StylingTask(
    private val doAsync: () -> List<SyntaxHighlightSpan>,
    private val onSuccess: (List<SyntaxHighlightSpan>) -> Unit
) : AsyncTask<Void, Void, Void>() {

    companion object {
        private const val TAG = "StylingTask"
    }

    private var syntaxHighlightSpans = listOf<SyntaxHighlightSpan>()

    private var cancelled = false
    private var isError = false

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            syntaxHighlightSpans = doAsync()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            isError = true
        }
        return null
    }

    override fun onPostExecute(voidR: Void?) {
        if (!isError && !isCancelled && !cancelled) {
            onSuccess(syntaxHighlightSpans)
        }
    }

    fun executeTask() {
        try {
            executeOnExecutor(THREAD_POOL_EXECUTOR, null)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    fun cancelTask(): Boolean {
        cancelled = true
        return cancel(true)
    }
}