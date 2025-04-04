import com.android.build.api.dsl.ManagedVirtualDevice

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
    id("com.blacksquircle.test")
    alias(libs.plugins.android.baselineprofile)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.blacksquircle.benchmark"
    targetProjectPath = ":app"

    testOptions.managedDevices.allDevices {
        create<ManagedVirtualDevice>("pixel6Api34") {
            device = "Pixel 6"
            sdkVersion = 34
            systemImageSource = "google_apis"
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api34"
    useConnectedDevices = false
}

dependencies {

    implementation(libs.test.junit)
    implementation(libs.test.junit.ext)
    implementation(libs.test.runner)
    implementation(libs.test.macrobenchmark)
}

androidComponents {
    onVariants { variant ->
        val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
        val applicationId = variant.testedApks.map {
            artifactsLoader.load(it)?.applicationId.orEmpty()
        }
        variant.instrumentationRunnerArguments.put("targetAppId", applicationId)
    }
}