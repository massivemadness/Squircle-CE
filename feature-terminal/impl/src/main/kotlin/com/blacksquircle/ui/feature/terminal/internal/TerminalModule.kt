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

package com.blacksquircle.ui.feature.terminal.internal

import android.content.Context
import com.blacksquircle.ui.feature.terminal.data.manager.SessionManager
import com.blacksquircle.ui.feature.terminal.data.repository.SessionRepositoryImpl
import com.blacksquircle.ui.feature.terminal.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides

@Module
internal object TerminalModule {

    @Provides
    @TerminalScope
    fun provideSessionRepository(
        sessionManager: SessionManager,
    ): SessionRepository {
        return SessionRepositoryImpl(
            sessionManager = sessionManager,
        )
    }

    @Provides
    @TerminalScope
    fun provideSessionManager(context: Context): SessionManager {
        return SessionManager(context)
    }
}