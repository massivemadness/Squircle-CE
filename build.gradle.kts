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

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.kover)
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.navigation) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    pluginManager.withPlugin("com.android.library") {
        koverMerge("debug")
    }
    pluginManager.withPlugin("com.android.application") {
        koverMerge("fdroidDebug")
    }
    rootProject.dependencies.add("kover", this)
}

fun Project.koverMerge(buildVariant: String) {
    koverReport {
        defaults {
            mergeWith(buildVariant)
        }
    }
}

// ./gradlew :koverHtmlReport
koverReport {
    filters {
        excludes {
            classes(
                // Android classes
                "*Application*",
                "*Activity*",
                "*Fragment*",
                "*Dialog*",
                "*Worker*",
                // Android generated
                "*.databinding.*",
                "*.BuildConfig",
                // Hilt generated
                "hilt_aggregated_deps.*",
                "*_Factory*",
                "*_Provide*Factory*",
                "*_HiltModules*",
                "*_MembersInjector*",
                // NavComponent generated
                "*FragmentArgs",
                "*FragmentArgs\$*",
                "*FragmentDirections",
                "*FragmentDirections\$*",
                // Room generated
                "*Dao_Impl",
                "*Dao_Impl\$*",
                // Code style
                "*App*",
                "*Extensions*",
                "*.internal.*",
                "*.model.*",
                "*.entity.*",
                "*.adapter.*",
                "*.customview.*",
                "*.view.*",
                "*.widget.*",
                "*.dialog.*",
                "*.fragment.*",
                "*.navigation.*",
                "*.lexer.*",
                "*.editorkit.*",
            )
        }
    }
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint(libs.ktlint) {
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
    )
}