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

package com.blacksquircle.ui.plugin.dirtytext

import android.text.Editable
import android.util.Log
import android.widget.EditText
import com.blacksquircle.ui.plugin.base.EditorPlugin

class DirtyTextPlugin : EditorPlugin(PLUGIN_ID) {

    var onChangeListener: OnChangeListener? = null

    private var isDirty = false

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        Log.d(PLUGIN_ID, "DirtyText plugin loaded successfully!")
    }

    override fun doAfterTextChanged(text: Editable?) {
        super.doAfterTextChanged(text)
        if (!isDirty) {
            onChangeListener?.onContentChanged()
        }
    }

    override fun setTextContent(text: CharSequence) {
        super.setTextContent(text)
        isDirty = false
    }

    companion object {
        const val PLUGIN_ID = "dirty-text-9124"
    }
}