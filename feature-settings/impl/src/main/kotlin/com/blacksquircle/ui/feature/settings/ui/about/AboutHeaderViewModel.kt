/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blacksquircle.ui.navigation.api.Navigator
import javax.inject.Inject
import javax.inject.Provider

internal class AboutHeaderViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    fun onBackClicked() {
        navigator.goBack()
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<AboutHeaderViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}