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

package com.blacksquircle.ui.feature.settings.data.utils

import android.content.Context
import android.content.pm.PackageManager

val Context.applicationName: String
    get() = try {
        applicationInfo.loadLabel(packageManager).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }

val Context.versionName: String
    get() = try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }

val Context.versionCode: Int
    get() = try {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }