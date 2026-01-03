/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.terminal.data.interactor

import android.content.Context
import android.content.Intent
import android.os.Build
import com.blacksquircle.ui.core.extensions.isPermissionGranted
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.terminal.api.interactor.TerminalInteractor
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.feature.terminal.api.model.ShellArgs
import com.termux.shared.termux.TermuxConstants.*
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.*
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.RUN_COMMAND_SERVICE.*
import timber.log.Timber

internal class TerminalInteractorImpl(
    private val settingsManager: SettingsManager,
    private val context: Context,
) : TerminalInteractor {

    override fun isTermux(): Boolean {
        val runtime = RuntimeType.of(settingsManager.terminalRuntime)
        return runtime == RuntimeType.TERMUX
    }

    override fun isTermuxInstalled(): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(TERMUX_PACKAGE_NAME)
            ?: return false
        val activities = context.packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }

    override fun isTermuxCompatible(): Boolean {
        val intent = Intent(ACTION_RUN_COMMAND).apply {
            setPackage(TERMUX_PACKAGE_NAME)
        }
        val services = context.packageManager.queryIntentServices(intent, 0)
        return services.isNotEmpty()
    }

    override fun isTermuxPermissionGranted(): Boolean {
        return context.isPermissionGranted(PERMISSION_RUN_COMMAND)
    }

    override fun openTermux(args: ShellArgs?) {
        runCatching {
            val intent = Intent(ACTION_RUN_COMMAND).apply {
                setClassName(TERMUX_PACKAGE_NAME, RUN_COMMAND_SERVICE_NAME)

                putExtra(EXTRA_COMMAND_PATH, TERMUX_PREFIX_DIR_PATH + TERMUX_SHELL)
                putExtra(EXTRA_WORKDIR, args?.workingDir)
                // https://github.com/termux/termux-app/commit/b94dc7eea93962f11b55cd9f5cb7aff715a1f4a6
                putExtra(EXTRA_BACKGROUND, false)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }.onFailure(Timber::e)
    }

    companion object {
        private const val TERMUX_SHELL = "/bin/bash"
    }
}