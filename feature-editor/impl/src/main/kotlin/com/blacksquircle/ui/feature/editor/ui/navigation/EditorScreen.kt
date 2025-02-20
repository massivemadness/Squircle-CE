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

package com.blacksquircle.ui.feature.editor.ui.navigation

import androidx.core.os.bundleOf
import com.blacksquircle.ui.core.extensions.NavAction
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.dialog.CloseModifiedDialog
import com.blacksquircle.ui.feature.editor.ui.dialog.ForceSyntaxDialog

internal sealed class EditorScreen(route: Any) : Screen(route) {

    data class CloseModifiedDialogScreen(val position: Int, val fileName: String) : EditorScreen(
        route = NavAction(
            id = R.id.closeModifiedDialog,
            args = bundleOf(
                CloseModifiedDialog.ARG_FILE_NAME to fileName,
                CloseModifiedDialog.ARG_POSITION to position,
            )
        ),
    )

    data class ForceSyntaxDialogScreen(val languageName: String) : EditorScreen(
        route = NavAction(
            id = R.id.forceSyntaxDialog,
            args = bundleOf(ForceSyntaxDialog.ARG_LANGUAGE to languageName)
        )
    )

    data object GotoLine : EditorScreen(R.id.gotoLineDialog)
    data object InsertColor : EditorScreen(R.id.insertColorDialog)
    data object ConfirmExit : EditorScreen(R.id.confirmExitDialog)
}