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
    id("com.blacksquircle.application")
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.baselineprofile)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.blacksquircle.ui"

    defaultConfig {
        applicationId = "com.blacksquircle.ui"
        versionCode = 10028
        versionName = "2025.1.3"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/versions/11/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/versions/15/OSGI-INF/MANIFEST.MF"
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":core-common"))
    implementation(project(":core-navigation:api"))
    implementation(project(":core-navigation:impl"))
    implementation(project(":core-redux"))
    implementation(project(":core-ui"))

    implementation(project(":feature-editor:api"))
    implementation(project(":feature-editor:impl"))
    implementation(project(":feature-explorer:api"))
    implementation(project(":feature-explorer:impl"))
    implementation(project(":feature-fonts:api"))
    implementation(project(":feature-fonts:impl"))
    implementation(project(":feature-git:api"))
    implementation(project(":feature-git:impl"))
    implementation(project(":feature-servers:api"))
    implementation(project(":feature-servers:impl"))
    implementation(project(":feature-settings:api"))
    implementation(project(":feature-settings:impl"))
    implementation(project(":feature-shortcuts:api"))
    implementation(project(":feature-shortcuts:impl"))
    implementation(project(":feature-terminal:api"))
    implementation(project(":feature-terminal:impl"))
    implementation(project(":feature-themes:api"))
    implementation(project(":feature-themes:impl"))
    baselineProfile(project(":benchmark"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodel.navigation)
    implementation(libs.androidx.profileinstaller)
    gmsImplementation(libs.google.appupdate)

    coreLibraryDesugaring(libs.android.tools.desugaring)

    implementation(libs.google.dagger)
    ksp(libs.google.dagger.compiler)

    testImplementation(project(":core-test"))
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.runner)
}

baselineProfile {
    mergeIntoMain = true
}