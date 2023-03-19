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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment

class CreateFileContract(
    fragment: Fragment,
    private val onResult: (ContractResult) -> Unit,
) {

    private var mimeType = "*/*"

    /**
     * [androidx.activity.result.contract.ActivityResultContracts.CreateDocument]
     */
    private val createDocument = fragment.registerForActivityResult(
        object : ActivityResultContract<String, Uri?>() {
            override fun createIntent(context: Context, input: String): Intent {
                return Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .setType(mimeType)
                    .putExtra(Intent.EXTRA_TITLE, input)
            }
            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
            }
            override fun getSynchronousResult(
                context: Context,
                input: String
            ): SynchronousResult<Uri?>? = null
        }
    ) { result ->
        if (result != null) {
            onResult(ContractResult.Success(result))
        } else {
            onResult(ContractResult.Canceled)
        }
    }

    fun launch(title: String, mimeType: String) {
        this.mimeType = mimeType
        createDocument.launch(title)
    }

    companion object {
        const val JSON = "application/json"
        const val TEXT = "text/*"
    }
}