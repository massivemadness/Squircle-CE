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

import com.blacksquircle.ui.BuildConst

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
    id("com.blacksquircle.stub")
}

android {
    compileSdk = BuildConst.COMPILE_SDK
    namespace = "com.blacksquircle.ui.core"

    defaultConfig {
        minSdk = BuildConst.MIN_SDK

        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
                argument("room.incremental", "true")
                argument("room.expandProjection", "true")
            }
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
        named("main") {
            java.srcDir("src/main/kotlin")
        }
        named("androidTest") {
            assets.srcDir(files("$projectDir/schemas"))
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Core
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.timber)

    // UI
    implementation(libs.androidx.appcompat)
    implementation(libs.materialdesign)

    // AAC
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    // Network
    implementation(libs.gson)

    // DI
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    // Modules
    implementation(project(":filesystems:filesystem-base"))

    implementation(project(":editorkit:language-base"))
    implementation(project(":editorkit:language-actionscript"))
    implementation(project(":editorkit:language-c"))
    implementation(project(":editorkit:language-cpp"))
    implementation(project(":editorkit:language-csharp"))
    implementation(project(":editorkit:language-css"))
    implementation(project(":editorkit:language-fortran"))
    implementation(project(":editorkit:language-go"))
    implementation(project(":editorkit:language-groovy"))
    implementation(project(":editorkit:language-html"))
    implementation(project(":editorkit:language-ini"))
    implementation(project(":editorkit:language-java"))
    implementation(project(":editorkit:language-javascript"))
    implementation(project(":editorkit:language-json"))
    implementation(project(":editorkit:language-julia"))
    implementation(project(":editorkit:language-kotlin"))
    implementation(project(":editorkit:language-latex"))
    implementation(project(":editorkit:language-lisp"))
    implementation(project(":editorkit:language-lua"))
    implementation(project(":editorkit:language-markdown"))
    implementation(project(":editorkit:language-php"))
    implementation(project(":editorkit:language-plaintext"))
    implementation(project(":editorkit:language-python"))
    implementation(project(":editorkit:language-ruby"))
    implementation(project(":editorkit:language-rust"))
    implementation(project(":editorkit:language-shell"))
    implementation(project(":editorkit:language-smali"))
    implementation(project(":editorkit:language-sql"))
    implementation(project(":editorkit:language-toml"))
    implementation(project(":editorkit:language-typescript"))
    implementation(project(":editorkit:language-visualbasic"))
    implementation(project(":editorkit:language-xml"))
    implementation(project(":editorkit:language-yaml"))

    // Tests
    implementation(libs.test.junit)
    implementation(libs.coroutines.test)
}