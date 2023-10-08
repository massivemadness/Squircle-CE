/*
 * Copyright 2023 Squircle CE contributors.
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

import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BenchmarkModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
                apply("org.jetbrains.kotlin.android")
            }

            configure<TestExtension> {
                compileSdk = BuildConst.COMPILE_SDK
                experimentalProperties["android.experimental.self-instrumenting"] = true

                defaultConfig {
                    minSdk = BuildConst.MIN_SDK
                    targetSdk = BuildConst.TARGET_SDK

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    missingDimensionStrategy("store", "fdroid")
                }
                buildTypes {
                    create("benchmark") {
                        isDebuggable = true
                        signingConfig = signingConfigs.getByName("debug")
                        matchingFallbacks += "release"
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                tasks.withType<KotlinCompile>().configureEach {
                    kotlinOptions {
                        jvmTarget = "17"
                    }
                }
                sourceSets {
                    named("main") {
                        java.srcDir("src/main/kotlin")
                    }
                }
            }
            configure<TestAndroidComponentsExtension> {
                beforeVariants {
                    it.enable = it.buildType == "benchmark"
                }
            }
        }
    }
}