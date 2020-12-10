/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.base.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * The [Context] object is obtained without invasion, which reduces code coupling.
 *
 * @author gzu-liyujiang (1032694760@qq.com)
 * @since 2020/12/10 10:06
 */
internal class ContextProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        initApplicationContext(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        unsupported()
    }

    override fun getType(uri: Uri): String? {
        unsupported()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        unsupported()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        unsupported()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        unsupported()
    }

    private fun unsupported(): Nothing = throw NotImplementedError("An operation is unsupported.")
}

private lateinit var applicationContext: Context

fun initApplicationContext(context: Context) {
    applicationContext = context.applicationContext
}

fun applicationContext(): Context {
    return applicationContext
}