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
    id("com.blacksquircle.application")
    alias(libs.plugins.android.baselineprofile)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.blacksquircle.ui"

    defaultConfig {
        applicationId = "com.blacksquircle.ui"
        versionCode = 10024
        versionName = "2025.1.0"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/**"
        }
    }
}

dependencies {

    // Core
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.timber)
    coreLibraryDesugaring(libs.android.desugaring)

    // Google Play
    googlePlayImplementation(libs.appupdate)

    // UI
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.adaptive)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.manifest)

    // AAC
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.navigation.compose)

    // Network
    implementation(libs.serialization)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // DI
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Modules
    implementation(project(":feature-changelog"))
    implementation(project(":feature-editor:api"))
    implementation(project(":feature-editor:impl"))
    implementation(project(":feature-explorer:api"))
    implementation(project(":feature-explorer:impl"))
    implementation(project(":feature-fonts:api"))
    implementation(project(":feature-fonts:impl"))
    implementation(project(":feature-servers:api"))
    implementation(project(":feature-servers:impl"))
    implementation(project(":feature-settings:api"))
    implementation(project(":feature-settings:impl"))
    implementation(project(":feature-shortcuts:api"))
    implementation(project(":feature-shortcuts:impl"))
    implementation(project(":feature-themes:api"))
    implementation(project(":feature-themes:impl"))
    implementation(project(":common-core"))
    implementation(project(":common-ui"))

    // Baseline Profile
    baselineProfile(project(":benchmark"))

    // Tests
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}

baselineProfile {
    mergeIntoMain = true
}