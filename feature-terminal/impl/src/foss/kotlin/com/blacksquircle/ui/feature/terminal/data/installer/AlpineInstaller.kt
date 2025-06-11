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

package com.blacksquircle.ui.feature.terminal.data.installer

import android.content.Context
import com.blacksquircle.ui.feature.terminal.domain.installer.RuntimeInstaller
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class AlpineInstaller @Inject constructor(
    // TODO inject retrofit
    private val context: Context,
) : RuntimeInstaller {

    override fun isInstalled(): Boolean {
        return false
    }

    override suspend fun install(): Flow<Float> {
        return flow {
            emit(0f)
            delay(500)
            emit(0.1f)
            delay(500)
            emit(0.2f)
            delay(500)
            emit(0.3f)
            delay(500)
            emit(0.4f)
            delay(500)
            emit(0.5f)
            delay(500)
            emit(0.6f)
            delay(500)
            emit(0.7f)
            delay(500)
            emit(0.8f)
            delay(500)
            emit(0.9f)
            delay(500)
            emit(1f)
        }
    }
}