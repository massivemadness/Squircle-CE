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

package com.blacksquircle.ui.core.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.blacksquircle.ui.core.service.ComponentService

inline fun <reified VM : ViewModel> Fragment.activityViewModels(
    crossinline viewModelProducer: () -> VM,
): Lazy<VM> {
    return viewModels(
        storeProducer = { requireActivity() },
        viewModelProducer = viewModelProducer,
    )
}

inline fun <reified VM : ViewModel> Fragment.viewModels(
    crossinline viewModelProducer: () -> VM,
): Lazy<VM> {
    return viewModels(
        storeProducer = { this },
        viewModelProducer = viewModelProducer,
    )
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModels(
    crossinline viewModelProducer: () -> VM,
): Lazy<VM> {
    return viewModels(
        storeProducer = { this },
        viewModelProducer = viewModelProducer,
    )
}

inline fun <reified VM : ViewModel> ComponentService.viewModels(
    crossinline viewModelProducer: () -> VM,
): Lazy<VM> {
    return viewModels(
        storeProducer = { this },
        viewModelProducer = viewModelProducer,
    )
}

inline fun <reified VM : ViewModel> viewModels(
    crossinline storeProducer: () -> ViewModelStoreOwner,
    crossinline viewModelProducer: () -> VM
): Lazy<VM> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return viewModelProducer() as VM
            }
        }
        val viewModelProvider = ViewModelProvider(storeProducer(), viewModelFactory)
        viewModelProvider[VM::class.java]
    }
}