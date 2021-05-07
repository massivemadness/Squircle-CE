/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.data.model.themes

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class ExternalTheme(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("author")
    val author: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("color_scheme")
    val externalScheme: ExternalScheme?
) {

    companion object {

        private val GSON_SERIALIZER = GsonBuilder()
            .setPrettyPrinting()
            .create()

        fun serialize(externalTheme: ExternalTheme): String {
            return GSON_SERIALIZER.toJson(externalTheme)
        }

        fun deserialize(themeJson: String): ExternalTheme {
            return GSON_SERIALIZER.fromJson(themeJson, ExternalTheme::class.java)
        }
    }
}