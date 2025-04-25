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
import com.blacksquircle.ui.feature.git.domain.exception.GitPullException
import com.blacksquircle.ui.feature.git.domain.exception.GitPushException
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.RemoteRefUpdate
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
) : GitRepository {

    override suspend fun currentBranch(repository: String): String {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                git.repository.fullBranch
            }
        }
    }

    override suspend fun branchList(repository: String): List<String> {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                git.branchList().call().map(Ref::getName)
            }
        }
    }

    override suspend fun changesList(repository: String): List<String> {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                val status = git.status().call()
                val changesList = mutableListOf<String>()
                changesList.addAll(status.added)
                changesList.addAll(status.changed)
                changesList.addAll(status.removed)
                changesList.addAll(status.missing)
                changesList.addAll(status.modified)
                changesList.addAll(status.untracked)
                changesList.addAll(status.conflicting)
                changesList
            }
        }
    }

    override suspend fun fetch(repository: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(repoDir).use { git ->
                git.fetch()
                    .setRemote(GIT_ORIGIN)
                    .setCredentialsProvider(credentialsProvider)
                    .call()
            }
        }
    }

    override suspend fun pull(repository: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(repoDir).use { git ->
                val pullResult = git.pull()
                    .setRemote(GIT_ORIGIN)
                    .setCredentialsProvider(credentialsProvider)
                    .call()

                if (!pullResult.isSuccessful) {
                    val errorMessage = buildString {
                        pullResult.mergeResult?.let { mergeResult ->
                            append("Merge status: ${mergeResult.mergeStatus}")
                            if (!mergeResult.mergeStatus.isSuccessful) {
                                append(", Conflicts: ${mergeResult.conflicts?.keys?.joinToString() ?: "none"}")
                            }
                        }
                        pullResult.rebaseResult?.let { rebaseResult ->
                            if (isNotEmpty()) append("; ")
                            append("Rebase status: ${rebaseResult.status}")
                        }
                    }
                    throw GitPullException(errorMessage)
                }
            }
        }
    }

    override suspend fun commit(
        repository: String,
        changes: List<String>,
        message: String,
        isAmend: Boolean
    ) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                changes.forEach { file ->
                    git.add().addFilepattern(file).call()
                }
                git.commit()
                    .setAuthor(settingsManager.gitUserName, settingsManager.gitUserEmail)
                    .setCommitter(settingsManager.gitUserName, settingsManager.gitUserEmail)
                    .setMessage(message)
                    .setAmend(isAmend)
                    .call()
            }
        }
    }

    override suspend fun push(repository: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            val credentialsProvider = UsernamePasswordCredentialsProvider(
                settingsManager.gitCredentialsUsername,
                settingsManager.gitCredentialsPassword
            )
            Git.open(repoDir).use { git ->
                val pushResults = git.push()
                    .setRemote(GIT_ORIGIN)
                    .setCredentialsProvider(credentialsProvider)
                    .call()

                val errorMessage = buildString {
                    for (result in pushResults) {
                        for (update in result.remoteUpdates) {
                            val ref = update.remoteName
                            val status = update.status
                            if (status != RemoteRefUpdate.Status.OK &&
                                status != RemoteRefUpdate.Status.UP_TO_DATE
                            ) {
                                if (isNotEmpty()) append("; ")
                                append("$ref: $status")
                                update.message?.let { append(" ($it)") }
                            }
                        }
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    throw GitPushException(errorMessage)
                }
            }
        }
    }

    override suspend fun checkout(repository: String, branchName: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                git.checkout()
                    .setName(branchName)
                    .call()
            }
        }
    }

    override suspend fun checkoutNew(repository: String, branchName: String, branchBase: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                git.checkout()
                    .setCreateBranch(true)
                    .setName(branchName)
                    .setStartPoint(branchBase)
                    .call()
            }
        }
    }

    companion object {
        private const val GIT_ORIGIN = "origin"
    }
}