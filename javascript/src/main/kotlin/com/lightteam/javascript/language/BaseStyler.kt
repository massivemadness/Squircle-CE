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

package com.lightteam.javascript.language

/*
import android.os.AsyncTask
import android.util.Log

abstract class BaseStyler : LanguageStyler {

    companion object {
        private const val TAG = "LanguageStyler"
    }

    lateinit var stringStream: StringStream

    private var task: StylingTask? = null
    private var cancelled = false
    private var hasParsed = false
    private var parseEnd = 0

    private var syntaxHighlightSpans: MutableList<SyntaxHighlightSpan> = mutableListOf()

    override fun cancelTask() {
        cancelled = true
        if (task != null) {
            task?.cancel(true)
        }
        task = null
    }

    override fun isTaskCancelled(): Boolean {
        if (cancelled) {
            return true
        }
        return task?.isCancelled ?: false
    }

    override fun runStyler(source: String) {
        if (hasParsed) {
            Log.e(TAG, "Tried to use a parser which has already been used")
            return
        }
        hasParsed = true
        Log.d(TAG, "runStyler() has been called. Starting styler.")
        //mDocument = DocumentsManager.getInstance().getDisplayedDocument()
        if (task != null) {
            task?.cancelTask()
        }
        task = StylingTask()
        stringStream = StringStream(source)
        parseEnd = stringStream.eof()
        try {
            task?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
        } catch (e: Exception) {
        }
    }

    inner class StylingTask : AsyncTask<Void, Void, Void>() {

        private var isError = false

        override fun doInBackground(vararg p0: Void?): Void? {
            syntaxHighlightSpans = mutableListOf()
            try {
                parse()
            } catch (e: Exception) {
                Log.e(TAG, "Error. Is cancelled? $isCancelled. Message: ${e.message}")
                isError = true
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (!isError && !isCancelled && !isTaskCancelled() */
/*&& mDocument != null*//*
) {
                //mDocument.setSpans(syntaxHighlightSpans)
                //DataController.getInstance().sendEvent(19)
            }
        }

        fun cancelTask(): Boolean {
            Log.d(TAG, "cancel() has been called. Cancelling styler task.")
            return cancel(true)
        }
    }
}*/
