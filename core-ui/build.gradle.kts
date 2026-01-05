/*
 * Copyright Squircle CE contributors.
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
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.blacksquircle.ui.ds"

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.skydoves.colorpicker.compose)

    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.tooling.preview)
    api(libs.androidx.compose.ui)
    debugApi(libs.androidx.compose.manifest)
    debugApi(libs.androidx.compose.tooling)
}