package com.blacksquircle.ui.feature.settings.data.converter

import com.blacksquircle.ui.feature.settings.ui.adapters.item.ReleaseModel
import java.util.regex.Pattern

object ReleaseConverter {

    private val RELEASE = Pattern.compile("<u>(.*?)(?=<br>\\n<br>)", Pattern.DOTALL)
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
                releaseNotes = releaseNotes
            )

            releases.add(releaseModel)
        }
        return releases
    }
}