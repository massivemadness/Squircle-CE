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

package com.blacksquircle.ui.feature.explorer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.ds.SquircleTheme

internal class ExplorerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        SquircleTheme {
            ExplorerScreen(navController = findNavController())
        }
    }

    companion object {

        const val KEY_AUTHENTICATION = "KEY_AUTHENTICATION"
        const val KEY_COMPRESS_FILE = "KEY_COMPRESS_FILE"
        const val KEY_CREATE_FILE = "KEY_CREATE_FILE"
        const val KEY_RENAME_FILE = "KEY_RENAME_FILE"
        const val KEY_DELETE_FILE = "KEY_DELETE_FILE"

        const val ARG_USER_INPUT = "ARG_USER_INPUT"
        const val ARG_IS_FOLDER = "ARG_IS_FOLDER"
    }
}