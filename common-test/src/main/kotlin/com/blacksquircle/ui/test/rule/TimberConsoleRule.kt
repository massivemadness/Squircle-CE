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

package com.blacksquircle.ui.test.rule

import com.blacksquircle.ui.test.logger.ConsoleTree
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

class TimberConsoleRule : TestWatcher() {

    private val consoleTree = ConsoleTree()

    override fun starting(description: Description) {
        Timber.plant(consoleTree)
    }

    override fun finished(description: Description) {
        Timber.uproot(consoleTree)
    }
}