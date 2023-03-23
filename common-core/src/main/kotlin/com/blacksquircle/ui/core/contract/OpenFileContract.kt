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

package com.blacksquircle.ui.core.contract

import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.fragment.app.Fragment

class OpenFileContract(
    fragment: Fragment,
    private val onResult: (ContractResult) -> Unit,
) {

    private val openDocument = fragment.registerForActivityResult(OpenDocument()) { result ->
        if (result != null) {
            onResult(ContractResult.Success(result))
        } else {
            onResult(ContractResult.Canceled)
        }
    }

    fun launch(vararg mimeTypes: String) {
        openDocument.launch(arrayOf(*mimeTypes))
    }

    companion object {
        const val JSON = "application/json"
        const val FONT = "font/*"
        const val X_FONT = "application/x-font-ttf"
        const val OCTET_STREAM = "application/octet-stream"
        const val PEM = "application/x-pem-file"
        const val ANY = "*/*"
    }
}