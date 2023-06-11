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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    compileOnly(libs.plugin.android)
    compileOnly(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("application-module") {
            id = "application-module"
            implementationClass = "ApplicationModulePlugin"
        }
        register("benchmark-module") {
            id = "benchmark-module"
            implementationClass = "BenchmarkModulePlugin"
        }
        register("feature-module") {
            id = "feature-module"
            implementationClass = "FeatureModulePlugin"
        }
        register("filesystem-module") {
            id = "filesystem-module"
            implementationClass = "FilesystemModulePlugin"
        }
        register("language-module") {
            id = "language-module"
            implementationClass = "LanguageModulePlugin"
        }
        register("publish-module") {
            id = "publish-module"
            implementationClass = "PublishModulePlugin"
        }
        register("stub-module") {
            id = "stub-module"
            implementationClass = "StubModulePlugin"
        }
    }
}