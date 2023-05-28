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

import com.blacksquircle.gradle.Gradle
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("stub-module")
}

android {
    compileSdk = Gradle.Build.compileSdk
    namespace = "com.blacksquircle.ui"

    defaultConfig {
        applicationId = "com.blacksquircle.ui"

        minSdk = Gradle.Build.minSdk
        targetSdk = Gradle.Build.targetSdk

        versionCode = 10018
        versionName = "2023.1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    setFlavorDimensions(listOf("store"))
    productFlavors {
        create("googlePlay") { dimension = "store" }
        create("fdroid") { dimension = "store" }
    }
    val properties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }
    signingConfigs {
        create("release") {
            storeFile = file("${properties["KEYSTORE_PATH"]}")
            storePassword = "${properties["KEYSTORE_PASSWORD"]}"
            keyAlias = "${properties["KEY_ALIAS"]}"
            keyPassword = "${properties["KEY_PASSWORD"]}"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("benchmark") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            proguardFiles("benchmark-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    sourceSets {
        named("androidTest") {
            java.srcDir("src/androidTest/kotlin")
        }
        named("fdroid") {
            java.srcDir("src/fdroid/kotlin")
        }
        named("googlePlay") {
            java.srcDir("src/googlePlay/kotlin")
        }
        named("main") {
            java.srcDir("src/main/kotlin")
        }
        named("test") {
            java.srcDir("src/test/kotlin")
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.timber)

    // Google Play
    val googlePlayImplementation by configurations
    googlePlayImplementation(libs.appupdate)

    // UI
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.materialdialogs.core)

    // AAC
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.navigation)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // DI
    implementation(libs.androidx.hilt.workmanager)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(":feature-changelog-api"))
    implementation(project(":feature-changelog-impl"))
    implementation(project(":feature-editor-api"))
    implementation(project(":feature-editor-impl"))
    implementation(project(":feature-explorer-api"))
    implementation(project(":feature-explorer-impl"))
    implementation(project(":feature-fonts-api"))
    implementation(project(":feature-fonts-impl"))
    implementation(project(":feature-servers-api"))
    implementation(project(":feature-servers-impl"))
    implementation(project(":feature-settings-api"))
    implementation(project(":feature-settings-impl"))
    implementation(project(":feature-shortcuts-api"))
    implementation(project(":feature-shortcuts-impl"))
    implementation(project(":feature-themes-api"))
    implementation(project(":feature-themes-impl"))
    implementation(project(":common-core"))
    implementation(project(":common-ui"))

    implementation(project(":filesystems:filesystem-base"))

    // Tests
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}