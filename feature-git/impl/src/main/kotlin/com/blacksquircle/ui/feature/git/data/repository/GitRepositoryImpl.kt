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

package com.blacksquircle.ui.feature.git.data.repository

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
) : GitRepository {

    override suspend fun fetch(repository: String) = callbackFlow {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            val git = Git.open(repoDir)
            val monitor = object : ProgressMonitor {
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String, totalWork: Int) {}
                override fun update(completed: Int) {
                    trySend(completed)
                }
                override fun endTask() {}
                override fun isCancelled(): Boolean = false
            }
            git.fetch()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .setProgressMonitor(monitor)
                .call()
            trySend(100)
        }
        awaitClose {}
    }

    override suspend fun pull(repository: String) = callbackFlow {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            val git = Git.open(repoDir)
            val monitor = object : ProgressMonitor {
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String, totalWork: Int) {}
                override fun update(completed: Int) {
                    trySend(completed)
                }
                override fun endTask() {}
                override fun isCancelled(): Boolean = false
            }
            git.pull()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .setProgressMonitor(monitor)
                .call()
            trySend(100)
        }
        awaitClose {}
    }

    override suspend fun commit(repository: String, message: String) = callbackFlow {
        withContext(dispatcherProvider.io()) {
            val git = Git.open(File(repository))
            git.add()
                .addFilepattern(GIT_ALL)
                .call()
            git.commit()
                .setMessage(message)
                .setAuthor(settingsManager.gitUserName, settingsManager.gitUserEmail)
                .setCommitter(settingsManager.gitUserName, settingsManager.gitUserEmail)
                .call()
            trySend(100)
        }
        awaitClose {}
    }

    override suspend fun push(repository: String) = callbackFlow {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            val git = Git.open(repoDir)
            val monitor = object : ProgressMonitor {
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String, totalWork: Int) {}
                override fun update(completed: Int) {
                    trySend(completed)
                }
                override fun endTask() {}
                override fun isCancelled(): Boolean = false
            }
            git.push()
                .setRemote(GIT_ORIGIN)
                .setCredentialsProvider(credentialsProvider)
                .setProgressMonitor(monitor)
                .call()
            trySend(100)
        }
        awaitClose {}
    }

    override suspend fun checkout(repository: String, branch: String) = callbackFlow {
        withContext(dispatcherProvider.io()) {
            val git = Git.open(File(repository))
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
            trySend(100)
        }
        awaitClose {}
    }

    companion object {
        private const val GIT_ORIGIN = "origin"
        private const val GIT_ALL = "."
    }
}