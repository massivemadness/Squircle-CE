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

package com.blacksquircle.ui.core.delegate

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> Fragment.viewBinding(
    noinline bindMethod: (View) -> T,
): ViewBindingDelegate<T> {
    return ViewBindingDelegate(this, bindMethod)
}

class ViewBindingDelegate<T : ViewBinding> @PublishedApi internal constructor(
    private val fragment: Fragment,
    private val bindMethod: (View) -> T,
) : ReadOnlyProperty<Any?, T> {

    private val handler = Handler(Looper.getMainLooper())
    private val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            handler.post { binding = null }
        }
    }

    private var binding: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner.lifecycle.addObserver(observer)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        binding ?: obtainBinding()

    private fun obtainBinding(): T {
        val view = checkNotNull(fragment.view) {
            "ViewBinding is only valid between onCreateView and onDestroyView."
        }
        return bindMethod.invoke(view)
            .also { binding = it }
    }
}