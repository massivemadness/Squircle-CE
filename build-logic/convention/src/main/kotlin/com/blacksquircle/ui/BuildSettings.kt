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

package com.blacksquircle.ui

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object BuildSettings {

    object Versions {
        const val MIN_SDK = 24
        const val TARGET_SDK = 36
        const val COMPILE_SDK = 36

        val JAVA = JavaVersion.VERSION_17
        val JVM_TARGET = JvmTarget.JVM_17
    }

    object R8 {
        const val ANDROID_RULES = "proguard-android-optimize.txt"
        const val PROGUARD_RULES = "proguard-rules.pro"
        const val CONSUMER_RULES = "consumer-rules.pro"
    }
}