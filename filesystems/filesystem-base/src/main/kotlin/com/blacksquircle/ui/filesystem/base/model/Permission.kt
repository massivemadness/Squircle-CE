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

package com.blacksquircle.ui.filesystem.base.model

@Retention(AnnotationRetention.SOURCE)
annotation class Permission {
    companion object {

        const val EMPTY = 0 // 000000000

        const val OWNER_READ = 1 // 000000001
        const val OWNER_WRITE = 1 shl 1 // 000000010
        const val OWNER_EXECUTE = 1 shl 2 // 000000100

        const val GROUP_READ = 1 shl 3 // 000001000
        const val GROUP_WRITE = 1 shl 4 // 000010000
        const val GROUP_EXECUTE = 1 shl 5 // 000100000

        const val OTHERS_READ = 1 shl 6 // 001000000
        const val OTHERS_WRITE = 1 shl 7 // 010000000
        const val OTHERS_EXECUTE = 1 shl 8 // 100000000
    }
}