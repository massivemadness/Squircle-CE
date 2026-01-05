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

package com.blacksquircle.ui.feature.explorer.data.workspace

import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.feature.explorer.data.mapper.WorkspaceMapper
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UserWorkspaceSource(workspaceDao: WorkspaceDao) : WorkspaceSource {

    override val workspaceFlow: Flow<List<WorkspaceModel>> =
        workspaceDao.flowAll().map { entities ->
            entities.map(WorkspaceMapper::toModel)
        }
}