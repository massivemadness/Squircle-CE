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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.ds.R as UiR

@Composable
@NonRestartableComposable
internal fun ErrorStatus(
    errorState: ErrorState?,
    modifier: Modifier = Modifier,
    onActionClicked: (ErrorAction) -> Unit = {},
) {
    if (errorState != null) {
        EmptyView(
            iconResId = errorState.icon,
            title = errorState.title,
            subtitle = errorState.subtitle,
            action = when (errorState.action) {
                ErrorAction.REQUEST_PERMISSIONS -> stringResource(UiR.string.common_grant_access)
                ErrorAction.ENTER_PASSWORD,
                ErrorAction.ENTER_PASSPHRASE -> stringResource(R.string.action_authenticate)
                else -> null
            },
            onClick = { onActionClicked(errorState.action) },
            modifier = modifier,
        )
    }
}

@PreviewLightDark
@Composable
private fun ExplorerErrorPreview() {
    PreviewBackground {
        ErrorStatus(
            errorState = ErrorState(
                title = stringResource(UiR.string.message_access_denied),
                subtitle = stringResource(UiR.string.message_access_required),
                action = ErrorAction.REQUEST_PERMISSIONS,
            ),
        )
    }
}