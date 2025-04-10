import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension

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

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.android.baselineprofile) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.kover)
}

// ./gradlew :app:koverHtmlReport
subprojects {
    afterEvaluate {
        val applyKover =
            pluginManager.hasPlugin("com.blacksquircle.application") ||
                pluginManager.hasPlugin("com.blacksquircle.feature") ||
                pluginManager.hasPlugin("com.blacksquircle.kotlin")
        if (applyKover) {
            apply(plugin = "org.jetbrains.kotlinx.kover")
            kover {
                applyRules()
            }
        }
    }
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint(libs.pinterest.ktlint) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

tasks.register<JavaExec>("ktlintCheck") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
        "!sora-editor/**" // Exclude submodule from ktlint
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
        "!sora-editor/**" // Exclude submodule from ktlint
    )
}

fun KoverProjectExtension.applyRules() {
    reports {
        filters {
            excludes {
                annotatedBy(
                    "kotlinx.serialization.Serializable",
                )
                classes(
                    // Android classes
                    "*Application*",
                    "*Activity*",
                    "*Fragment*",
                    "*Dialog*",
                    "*Worker*",
                    "*Service*",
                    "*BroadcastReceiver*",
                    // Android generated
                    "*.databinding.*",
                    "*.BuildConfig",
                    // Dagger
                    "*Component",
                    "*Component\$*",
                    "*Module",
                    "*Module\$*",
                    "*Scope",
                    "*_MembersInjector",
                    // Dagger generated
                    "*Dagger*",
                    "*_Provide*Factory*",
                    "*_Factory*",
                    // Room
                    "*.database.*",
                    "*.dao.*",
                    // UI
                    "*App*",
                    "*GraphKt*",
                    "*GraphKt\$*",
                    "*ScreenKt*",
                    "*ScreenKt\$*",
                    "*Composable*",
                    "*Extensions*",
                    "*ViewModel\$Factory",
                    "*ViewModel\$ParameterizedFactory",
                    // Code style
                    "*.api.*",
                    "*.internal.*",
                    "*.model.*",
                    "*.entity.*",
                    "*.exception.*",
                    "*.compose.*",
                    "*.core.*",
                    "*.ds.*",
                    "*.view.*",
                    "*.menu.*",
                )
            }
        }
    }
}