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
import com.blacksquircle.ui.feature.git.domain.model.ChangeType
import com.blacksquircle.ui.feature.git.domain.model.GitChange
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.DetachedHeadException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.RemoteRefUpdate
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import timber.log.Timber
import java.io.File

internal class GitRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager
) : GitRepository {

    override suspend fun currentBranch(repository: String): String {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                git.currentHead()
            }
        }
    }

    override suspend fun branchList(repository: String): List<String> {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                val branches = mutableListOf<String>()
                val refs = git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()

                for (ref in refs) {
                    val name = Repository.shortenRefName(ref.name)
                    branches.add(name)
                }

                val current = git.currentHead()
                if (current !in branches) {
                    branches.add(0, current)
                }

                branches
            }
        }
    }

    override suspend fun changesList(repository: String): List<GitChange> {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                buildList {
                    val status = git.status().call()
                    addAll(status.added.map { GitChange(it, ChangeType.ADDED) })
                    addAll(status.changed.map { GitChange(it, ChangeType.MODIFIED) })
                    addAll(status.removed.map { GitChange(it, ChangeType.DELETED) })
                    addAll(status.missing.map { GitChange(it, ChangeType.DELETED) })
                    addAll(status.modified.map { GitChange(it, ChangeType.MODIFIED) })
                    addAll(status.untracked.map { GitChange(it, ChangeType.ADDED) })
                    addAll(status.conflicting.map { GitChange(it, ChangeType.MODIFIED) })
                }
            }
        }
    }

    override suspend fun commitCount(repository: String): Int {
        return withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                val repositoryObject = git.repository
                val branch = repositoryObject.branch
                val localRef = repositoryObject.findRef("refs/heads/$branch")
                val remoteRef = repositoryObject.findRef("refs/remotes/$GIT_ORIGIN/$branch")
                if (remoteRef == null) {
                    Timber.w("No remote tracking branch found.")
                    return@withContext -1
                }

                val localCommit = localRef.objectId
                val remoteCommit = remoteRef.objectId

                RevWalk(repositoryObject).use { walk ->
                    val local = walk.parseCommit(localCommit)
                    val remote = walk.parseCommit(remoteCommit)

                    walk.markStart(local)
                    walk.markUninteresting(remote)

                    walk.count()
                }
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
                    .setCheckFetchedObjects(true)
                    .setRemoveDeletedRefs(true)
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
        changes: List<GitChange>,
        message: String,
        isAmend: Boolean
    ) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                changes.forEach { change ->
                    when (change.changeType) {
                        ChangeType.ADDED -> git.add().addFilepattern(change.name).call()
                        ChangeType.MODIFIED -> git.add().addFilepattern(change.name).call()
                        ChangeType.DELETED -> git.rm().addFilepattern(change.name).call()
                    }
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

    override suspend fun push(repository: String, force: Boolean) {
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
                    .setForce(force)
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
                if (branchName.startsWith("$GIT_ORIGIN/")) {
                    val localBranchName = branchName.removePrefix("$GIT_ORIGIN/")
                    val existingBranches = git.branchList().call().map { it.name }
                    if ("refs/heads/$localBranchName" !in existingBranches) {
                        git.checkout()
                            .setCreateBranch(true)
                            .setName(localBranchName)
                            .setStartPoint(branchName)
                            .call()
                    } else {
                        git.checkout().setName(localBranchName).call()
                    }
                } else {
                    git.checkout().setName(branchName).call()
                }
            }
        }
    }

    override suspend fun checkoutNew(repository: String, branchName: String, branchBase: String) {
        withContext(dispatcherProvider.io()) {
            val repoDir = File(repository)
            Git.open(repoDir).use { git ->
                if (branchBase.startsWith("$GIT_ORIGIN/")) {
                    git.checkout()
                        .setName(branchName)
                        .setStartPoint(branchBase)
                        .setCreateBranch(true)
                        .call()
                } else {
                    git.checkout()
                        .setName(branchName)
                        .setStartPoint("refs/heads/$branchBase")
                        .setCreateBranch(true)
                        .call()
                }
            }
        }
    }

    private fun Git.currentHead(): String {
        return try {
            repository.branch
        } catch (e: DetachedHeadException) {
            val fullCommitId = repository.fullBranch
            if (fullCommitId != null && fullCommitId.length >= 7) {
                fullCommitId.substring(0, 7)
            } else {
                fullCommitId.toString()
            }
        }
    }

    companion object {
        private const val GIT_ORIGIN = "origin"
    }
}