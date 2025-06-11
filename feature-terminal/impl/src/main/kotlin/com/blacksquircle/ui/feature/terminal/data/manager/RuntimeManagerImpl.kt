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

package com.blacksquircle.ui.feature.terminal.data.manager

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.feature.terminal.api.model.TerminalRuntime
import com.blacksquircle.ui.feature.terminal.domain.installer.RuntimeInstaller
import com.blacksquircle.ui.feature.terminal.domain.manager.RuntimeManager
import com.blacksquircle.ui.feature.terminal.domain.model.RuntimeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class RuntimeManagerImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val runtimeSet: Set<TerminalRuntime>,
    private val installerMap: Map<RuntimeType, RuntimeInstaller>,
) : RuntimeManager {

    override fun createRuntime(): Flow<RuntimeState> = flow {
        val currentType = RuntimeType.of(settingsManager.terminalRuntime)
        val terminalRuntime = runtimeSet.find { it.type == currentType }
        if (terminalRuntime == null) {
            emit(RuntimeState.Failed("Unsupported runtime"))
            return@flow
        }

        val installer = installerMap[currentType]
        if (installer == null || installer.isInstalled()) {
            emit(RuntimeState.Ready(terminalRuntime))
            return@flow
        }

        installer.install().collect { progress ->
            emit(RuntimeState.Installing(progress))
        }

        emit(RuntimeState.Ready(terminalRuntime))
    }.catch { e ->
        emit(RuntimeState.Failed(e.message.orEmpty()))
    }.flowOn(dispatcherProvider.io())
}