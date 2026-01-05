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
    alias(libs.plugins.android.room)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.blacksquircle.ui.core"

    room {
        schemaDirectory("$projectDir/schemas")
        generateKotlin = true
    }
    sourceSets {
        named("androidTest") {
            assets.srcDir("$projectDir/schemas")
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":core-ui"))

    implementation(libs.androidx.lifecycle.service)

    api(libs.androidx.core)
    api(libs.androidx.navigation.ui)
    api(libs.jakewharton.timber)
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization)
    api(libs.squareup.retrofit)
    api(libs.squareup.retrofit.converter)

    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.google.dagger)
    ksp(libs.google.dagger.compiler)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}