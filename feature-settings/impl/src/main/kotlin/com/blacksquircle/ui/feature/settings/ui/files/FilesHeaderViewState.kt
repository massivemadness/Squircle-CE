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

package com.blacksquircle.ui.feature.settings.ui.files

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState

@Immutable
internal data class FilesHeaderViewState(
    val encodingAutoDetect: Boolean,
    val encodingForOpening: String,
    val encodingForSaving: String,
    val encodingList: List<String>,
    val lineBreakForSaving: String,
    val showHidden: Boolean,
    val compactPackages: Boolean,
    val foldersOnTop: Boolean,
    val sortMode: String,
) : ViewState