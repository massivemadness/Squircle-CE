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
import com.blacksquircle.ui.feature.editor.domain.repository.GitRepository
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class InvalidCredentialsException : Exception("Missing Git credentials or user info")
class RepositoryNotFoundException : Exception("Git repository not found")

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
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

    override suspend fun fetch(path: String) {
        val credentialsProvider =
            UsernamePasswordCredentialsProvider(settingsManager.gitCredentialsUsername, settingsManager.gitCredentialsToken)
        Git.open(File(path)).fetch().setRemote("origin").setCredentialsProvider(credentialsProvider).call()
    }

    override suspend fun pull(path: String) {
        val credentialsProvider =
            UsernamePasswordCredentialsProvider(settingsManager.gitCredentialsUsername, settingsManager.gitCredentialsToken)
        Git.open(File(path)).pull().setRemote("origin").setCredentialsProvider(credentialsProvider).call()
    }

    override suspend fun commit(path: String, text: String) {
        val git = Git.open(File(path))
        git.add().addFilepattern(".").call()
        git.commit().setMessage(text).setAuthor(settingsManager.gitUserName, settingsManager.gitUserEmail).setCommitter(settingsManager.gitUserName, settingsManager.gitUserEmail).call()
    }

    override suspend fun push(path: String) {
        val credentialsProvider =
            UsernamePasswordCredentialsProvider(settingsManager.gitCredentialsUsername, settingsManager.gitCredentialsToken)
        Git.open(File(path)).push().setRemote("origin").setCredentialsProvider(credentialsProvider).call()
    }

    override suspend fun checkout(path: String, branch: String) {
        val git = Git.open(File(path))
        try {
            git.checkout().setName(branch).call()
        } catch (e: RefNotFoundException) {
            git.checkout().setCreateBranch(true).setName(branch).call()
        }
    }
}