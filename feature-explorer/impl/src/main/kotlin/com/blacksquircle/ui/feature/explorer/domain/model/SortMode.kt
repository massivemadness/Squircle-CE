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

package com.blacksquircle.ui.feature.explorer.domain.model

internal enum class SortMode(val value: String) {
    SORT_BY_NAME("sort_by_name"),
    SORT_BY_SIZE("sort_by_size"),
    SORT_BY_DATE("sort_by_date");

    companion object {

        fun of(value: String): SortMode {
            return entries.find { it.value == value } ?: SORT_BY_NAME
        }
    }
}