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

package com.blacksquircle.ui.feature.git.ui.commit

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.git.domain.model.GitChange

@Immutable
internal data class CommitViewState(
    val changesList: List<GitChange> = emptyList(),
    val selectedChanges: List<GitChange> = emptyList(),
    val isCommitting: Boolean = false,
    val isAmend: Boolean = false,
    val isLoading: Boolean = true,
    val commitMessage: String = "",
    val errorMessage: String = "",
) : ViewState {

    val isError: Boolean
        get() = errorMessage.isNotEmpty() && !isLoading

    val isCommitButtonEnabled: Boolean
        get() = selectedChanges.isNotEmpty() &&
            commitMessage.isNotBlank() &&
            !isCommitting &&
            !isError
}