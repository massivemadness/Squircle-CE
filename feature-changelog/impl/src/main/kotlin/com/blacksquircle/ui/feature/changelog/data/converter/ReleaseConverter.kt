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

package com.blacksquircle.ui.feature.changelog.data.converter

import com.blacksquircle.ui.feature.changelog.domain.model.ReleaseModel
import java.util.regex.Pattern

object ReleaseConverter {

    private val RELEASE = Pattern.compile("<b>(.*?)(?=<br>\\n<br>)", Pattern.DOTALL)
    private val VERSION_NAME = Pattern.compile("v(.*?)(?=,)")
    private val RELEASE_DATE = Pattern.compile("(?<=, )\\d(.*?)(?=</b>)")
    private val RELEASE_NOTES = Pattern.compile("• (.*?)$", Pattern.MULTILINE)

    fun toReleaseModels(text: String): List<ReleaseModel> {
        val releases = mutableListOf<ReleaseModel>()
        val releaseMatcher = RELEASE.matcher(text)

        while (releaseMatcher.find()) {
            val region = text.substring(releaseMatcher.start() until releaseMatcher.end())
            var versionName = ""
            var releaseDate = ""
            var releaseNotes = ""

            var matcher = VERSION_NAME.matcher(region)
            if (matcher.find()) {
                versionName = region.substring(matcher.start() until matcher.end())
            }

            matcher = RELEASE_DATE.matcher(region)
            if (matcher.find()) {
                releaseDate = region.substring(matcher.start() until matcher.end())
            }

            matcher = RELEASE_NOTES.matcher(region)
            while (matcher.find()) {
                val substring = region.substring(matcher.start() until matcher.end())
                releaseNotes += substring
            }

            val releaseModel = ReleaseModel(
                versionName = versionName,
                releaseDate = releaseDate,
                releaseNotes = releaseNotes,
            )

            releases.add(releaseModel)
        }
        return releases
    }
}