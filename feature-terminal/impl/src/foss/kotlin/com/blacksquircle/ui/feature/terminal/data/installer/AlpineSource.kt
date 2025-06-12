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

package com.blacksquircle.ui.feature.terminal.data.installer

import android.os.Build

internal object AlpineSource {

    val DOWNLOAD_LINK: String
        get() {
            val deviceAbi = Build.SUPPORTED_ABIS
            return if (deviceAbi.contains("x86_64")) {
                ALPINE_X86_64
            } else if (deviceAbi.contains("arm64-v8a")) {
                ALPINE_AARCH64
            } else if (deviceAbi.contains("armeabi-v7a")) {
                ALPINE_ARM
            } else {
                throw RuntimeException("Unsupported CPU (${deviceAbi.joinToString()})")
            }
        }

    private const val ALPINE_ARM =
        "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/armhf/alpine-minirootfs-3.22.0-armhf.tar.gz"
    private const val ALPINE_AARCH64 =
        "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/aarch64/alpine-minirootfs-3.22.0-aarch64.tar.gz"
    private const val ALPINE_X86_64 =
        "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/x86_64/alpine-minirootfs-3.22.0-x86_64.tar.gz"
}