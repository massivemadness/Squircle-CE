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

package com.blacksquircle.ui.feature.git.data.interactor

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.git.api.exception.InvalidCredentialsException
import com.blacksquircle.ui.feature.git.api.exception.RepositoryNotFoundException
import com.blacksquircle.ui.feature.git.api.exception.UnsupportedFilesystemException
import com.blacksquircle.ui.feature.git.api.interactor.GitInteractor
import com.blacksquircle.ui.filesystem.base.exception.FileNotFoundException
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

internal class GitInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
) : GitInteractor {

    override suspend fun getRepoPath(fileModel: FileModel): String {
        return withContext(dispatcherProvider.io()) {
            if (fileModel.filesystemUuid != LocalFilesystem.LOCAL_UUID) {
                throw UnsupportedFilesystemException()
            }

            if (settingsManager.gitCredentialsUsername.isBlank() ||
                settingsManager.gitCredentialsPassword.isBlank() ||
                settingsManager.gitUserEmail.isBlank() ||
                settingsManager.gitUserName.isBlank()
            ) {
                throw InvalidCredentialsException()
            }

            var current: File? = File(fileModel.path)
            while (current?.parentFile != null) {
                val gitDir = File(current, GIT_FOLDER)
                if (gitDir.exists() && gitDir.isDirectory) {
                    return@withContext current.absolutePath
                }
                current = current.parentFile
            }

            throw RepositoryNotFoundException()
        }
    }

    override suspend fun cloneRepository(
        fileModel: FileModel,
        url: String,
        submodules: Boolean,
    ): Flow<String> {
        return callbackFlow {
            if (fileModel.filesystemUuid != LocalFilesystem.LOCAL_UUID) {
                throw UnsupportedFilesystemException()
            }

            if (settingsManager.gitCredentialsUsername.isBlank() ||
                settingsManager.gitCredentialsPassword.isBlank() ||
                settingsManager.gitUserEmail.isBlank() ||
                settingsManager.gitUserName.isBlank()
            ) {
                throw InvalidCredentialsException()
            }

            val file = File(fileModel.path)
            if (!file.exists() || !file.isDirectory) {
                throw FileNotFoundException(fileModel.path)
            }

            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )

            Git.cloneRepository()
                .setURI(url)
                .setDirectory(file)
                .setCloneSubmodules(submodules)
                .setCredentialsProvider(credentialsProvider)
                .setProgressMonitor(object : ProgressMonitor {
                    override fun start(totalTasks: Int) = Unit
                    override fun beginTask(title: String?, totalWork: Int) {
                        trySend(title.orEmpty())
                    }
                    override fun update(completed: Int) = Unit
                    override fun endTask() {
                        close()
                    }
                    override fun isCancelled(): Boolean {
                        return false
                    }
                })
                .call()

            awaitClose()
        }
    }

    companion object {
        private const val GIT_FOLDER = ".git"
    }
}