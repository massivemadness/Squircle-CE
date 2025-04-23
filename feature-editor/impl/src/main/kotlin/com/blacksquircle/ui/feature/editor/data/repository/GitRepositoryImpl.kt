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

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.data.exception.InvalidCredentialsException
import com.blacksquircle.ui.feature.editor.data.exception.RepositoryNotFoundException
import com.blacksquircle.ui.feature.editor.domain.repository.GitRepository
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
) : GitRepository {

    override suspend fun getRepoPath(path: String): String {
        return withContext(dispatcherProvider.io()) {
            if (settingsManager.gitCredentialsUsername.isBlank() ||
                settingsManager.gitCredentialsPassword.isBlank() ||
                settingsManager.gitUserEmail.isBlank() ||
                settingsManager.gitUserName.isBlank()
            ) {
                throw InvalidCredentialsException()
            }

            var current: File? = File(path)
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

    override suspend fun fetch(repoPath: String) {
        withContext(dispatcherProvider.io()) {
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(File(repoPath))
                .fetch()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .call()
        }
    }

    override suspend fun pull(repoPath: String) {
        withContext(dispatcherProvider.io()) {
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(File(repoPath))
                .pull()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .call()
        }
    }

    override suspend fun commit(repoPath: String, text: String) {
        withContext(dispatcherProvider.io()) {
            val git = Git.open(File(repoPath))
            git.add()
                .addFilepattern(GIT_ALL)
                .call()
            git.commit()
                .setMessage(text)
                .setAuthor(settingsManager.gitUserName, settingsManager.gitUserEmail)
                .setCommitter(settingsManager.gitUserName, settingsManager.gitUserEmail)
                .call()
        }
    }

    override suspend fun push(repoPath: String) {
        withContext(dispatcherProvider.io()) {
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(File(repoPath))
                .push()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .call()
        }
    }

    override suspend fun checkout(repoPath: String, branch: String) {
        withContext(dispatcherProvider.io()) {
            val git = Git.open(File(repoPath))
            try {
                git.checkout()
                    .setName(branch)
                    .call()
            } catch (e: RefNotFoundException) {
                git.checkout()
                    .setCreateBranch(true)
                    .setName(branch)
                    .call()
            }
        }
    }

    companion object {
        private const val GIT_FOLDER = ".git"
        private const val GIT_ORIGIN = "origin"
        private const val GIT_ALL = "."
    }
}