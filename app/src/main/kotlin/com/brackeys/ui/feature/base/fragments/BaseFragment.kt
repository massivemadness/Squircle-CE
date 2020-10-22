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

package com.brackeys.ui.feature.base.fragments

import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    private val viewCompositeDisposable by lazy { CompositeDisposable() }

    override fun onDestroyView() {
        super.onDestroyView()
        viewCompositeDisposable.clear()
    }

    protected fun closeKeyboard() {
        val inputManager = requireContext().getSystemService<InputMethodManager>()
        val windowToken = requireActivity().currentFocus?.windowToken
        val hideType = InputMethodManager.HIDE_NOT_ALWAYS
        inputManager?.hideSoftInputFromWindow(windowToken, hideType)
    }

    protected fun showToast(@StringRes textRes: Int = -1, text: String = "", duration: Int = Toast.LENGTH_SHORT) {
        if (textRes != -1) {
            Toast.makeText(context, textRes, duration).show()
        } else {
            Toast.makeText(context, text, duration).show()
        }
    }

    protected fun Disposable.disposeOnFragmentDestroyView(): Disposable {
        viewCompositeDisposable.add(this)
        return this
    }
}