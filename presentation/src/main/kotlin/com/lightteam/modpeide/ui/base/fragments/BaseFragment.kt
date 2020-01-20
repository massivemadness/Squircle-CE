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

package com.lightteam.modpeide.ui.base.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : DaggerFragment() {

    private val viewCompositeDisposable by lazy { CompositeDisposable() }

    abstract fun layoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewCompositeDisposable.clear()
    }

    protected fun showToast(@StringRes textRes: Int = -1, text: String = "", duration: Int = Toast.LENGTH_SHORT) {
        if(textRes != -1) {
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