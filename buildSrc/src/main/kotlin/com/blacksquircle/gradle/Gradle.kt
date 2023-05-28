package com.blacksquircle.gradle

object Gradle {

    object Build {
        const val minSdk = 24
        const val targetSdk = 33
        const val compileSdk = 33
        const val buildTools = "33.0.2"
    }

    object Maven {

        const val libraryVersionName = "2.7.0"
        const val libraryVersionCode = 18

        var libraryGroupId = ""
        var libraryArtifactId = ""
    }
}