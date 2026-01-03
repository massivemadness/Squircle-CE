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

package com.blacksquircle.ui.feature.terminal.data.runtime

import android.os.Environment
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.feature.terminal.domain.runtime.TerminalRuntime

internal object AndroidRuntime : TerminalRuntime {

    override val name = "Android"
    override val type = RuntimeType.ANDROID

    override val shellPath: String
        get() = "/system/bin/sh"
    override val homeDir: String
        get() = Environment.getExternalStorageDirectory().absolutePath
    override val tmpDir: String
        get() = "/data/local/tmp"
}