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

package com.blacksquircle.ui.core.contract

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.compose.runtime.Composable

@Composable
fun rememberOpenFolderContract(
    onResult: (ContractResult) -> Unit
): ManagedActivityResultLauncher<Uri?, Uri?> {
    return rememberLauncherForActivityResult(OpenDocumentTree()) { result ->
        if (result != null) {
            onResult(ContractResult.Success(result))
        } else {
            onResult(ContractResult.Canceled)
        }
    }
}