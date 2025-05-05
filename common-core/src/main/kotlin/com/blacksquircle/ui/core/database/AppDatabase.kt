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

package com.blacksquircle.ui.core.database

import com.blacksquircle.ui.core.database.dao.document.DocumentDao
import com.blacksquircle.ui.core.database.dao.font.FontDao
import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao

interface AppDatabase {
    fun documentDao(): DocumentDao
    fun fontDao(): FontDao
    fun serverDao(): ServerDao
    fun workspaceDao(): WorkspaceDao
}