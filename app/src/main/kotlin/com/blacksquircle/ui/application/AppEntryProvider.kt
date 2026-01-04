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

package com.blacksquircle.ui.application

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.blacksquircle.ui.application.update.UpdateScreen
import com.blacksquircle.ui.navigation.api.provider.EntryProvider

internal class AppEntryProvider : EntryProvider {

    override fun EntryProviderScope<NavKey>.builder() {
        entry<UpdateRoute>(metadata = DialogSceneStrategy.dialog()) {
            UpdateScreen()
        }
    }
}