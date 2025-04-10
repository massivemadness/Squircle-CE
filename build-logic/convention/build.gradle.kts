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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.plugin.android)
    compileOnly(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("com.blacksquircle.application") {
            id = "com.blacksquircle.application"
            implementationClass = "com.blacksquircle.ui.ApplicationModulePlugin"
        }
        register("com.blacksquircle.test") {
            id = "com.blacksquircle.test"
            implementationClass = "com.blacksquircle.ui.TestModulePlugin"
        }
        register("com.blacksquircle.feature") {
            id = "com.blacksquircle.feature"
            implementationClass = "com.blacksquircle.ui.FeatureModulePlugin"
        }
        register("com.blacksquircle.kotlin") {
            id = "com.blacksquircle.kotlin"
            implementationClass = "com.blacksquircle.ui.KotlinModulePlugin"
        }
        register("com.blacksquircle.publish") {
            id = "com.blacksquircle.publish"
            implementationClass = "com.blacksquircle.ui.PublishModulePlugin"
        }
        register("com.blacksquircle.lint") {
            id = "com.blacksquircle.lint"
            implementationClass = "com.blacksquircle.ui.LintConventionPlugin"
        }
    }
}