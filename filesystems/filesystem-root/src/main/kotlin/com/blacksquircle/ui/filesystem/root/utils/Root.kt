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

package com.blacksquircle.ui.filesystem.root.utils

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TIMEOUT = 20_000L

internal fun requestRootAccess(): Shell {
    return runBlocking {
        withTimeout(TIMEOUT) {
            suspendCoroutine { cont ->
                try {
                    cont.resume(Shell.getShell())
                } catch (e: Throwable) {
                    cont.resumeWithException(e)
                }
            }
        }
    }
}