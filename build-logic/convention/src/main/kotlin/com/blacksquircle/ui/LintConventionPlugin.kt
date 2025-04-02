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

package com.blacksquircle.ui

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

class LintConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val rootPath = target.rootDir.path
        val baselineRules = File("$rootPath/app/lint-baseline.xml")

        with(target) {
            pluginManager.withPlugin("com.android.application") {
                configure<ApplicationExtension> {
                    lint.configure(baselineRules)
                }
            }
            pluginManager.withPlugin("com.android.library") {
                configure<LibraryExtension> {
                    lint.configure(baselineRules)
                }
            }
        }
    }

    private fun Lint.configure(baselineRules: File) {
        baseline = baselineRules
        abortOnError = false
        ignoreWarnings = true
        checkReleaseBuilds = false
        checkDependencies = false
    }
}