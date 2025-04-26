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

rootProject.name = "Squircle-CE"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("sora-editor")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

include(":app", ":benchmark")

include(
    ":common-core",
    ":common-test",
    ":common-ui",
    ":feature-editor:api",
    ":feature-editor:impl",
    ":feature-explorer:api",
    ":feature-explorer:impl",
    ":feature-fonts:api",
    ":feature-fonts:impl",
    ":feature-git:api",
    ":feature-git:impl",
    ":feature-servers:api",
    ":feature-servers:impl",
    ":feature-settings:api",
    ":feature-settings:impl",
    ":feature-shortcuts:api",
    ":feature-shortcuts:impl",
    ":feature-themes:api",
    ":feature-themes:impl",
)

include(
    ":filesystems:filesystem-base",
    ":filesystems:filesystem-local",
    ":filesystems:filesystem-root",
    ":filesystems:filesystem-saf",
    ":filesystems:filesystem-ftp",
    ":filesystems:filesystem-ftps",
    ":filesystems:filesystem-sftp",
    // TODO ":filesystems:filesystem-dropbox",
    // TODO ":filesystems:filesystem-googledrive",
)

includeBuild("sora-editor")