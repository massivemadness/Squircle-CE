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

package com.blacksquircle.ui.feature.editor.data.repository

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.domain.repository.GitRepository
import kotlinx.coroutines.withContext
import java.io.File

class InvalidCredentialsException : Exception("Missing Git credentials or user info")
class RepositoryNotFoundException : Exception("Git repository not found")

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val context: Context,
) : GitRepository {

    override suspend fun getRepoPath(path: String): String = withContext(dispatcherProvider.io()) {
        if (settingsManager.gitCredentialsUsername.isEmpty() ||
            settingsManager.gitCredentialsToken.isEmpty() ||
            settingsManager.gitUserEmail.isEmpty() ||
            settingsManager.gitUserName.isEmpty()
        ) {
            throw InvalidCredentialsException()
        }

        var current = File(path)
        while (current.parentFile != null) {
            val gitDir = File(current, ".git")
            if (gitDir.exists() && gitDir.isDirectory) {
                return@withContext current.absolutePath
            }
            current = current.parentFile
        }

        throw RepositoryNotFoundException()
    }
}