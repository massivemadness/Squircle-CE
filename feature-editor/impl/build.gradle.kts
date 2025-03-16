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
    id("com.blacksquircle.feature")
    alias(libs.plugins.android.navigation)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.blacksquircle.ui.feature.editor"

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.timber)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.adaptive)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.manifest)
    implementation(libs.reorderable)
    implementation(libs.sora.editor)

    // AAC
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.navigation) // xml
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    // DI
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Modules
    implementation(project(":feature-editor:api"))
    implementation(project(":feature-explorer:api"))
    implementation(project(":feature-fonts:api"))
    implementation(project(":feature-shortcuts:api"))
    implementation(project(":feature-themes:api"))
    implementation(project(":common-core"))
    implementation(project(":common-ui"))

    implementation(project(":filesystems:filesystem-local"))
    implementation(project(":filesystems:filesystem-saf"))

    // Tests
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}