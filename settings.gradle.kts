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

pluginManagement {
    includeBuild("build-logic")
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
    ":common-ui",
    ":feature-changelog:api",
    ":feature-changelog:impl",
    ":feature-editor:api",
    ":feature-editor:impl",
    ":feature-explorer:api",
    ":feature-explorer:impl",
    ":feature-fonts:api",
    ":feature-fonts:impl",
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
    ":filesystems:filesystem-ftp",
    ":filesystems:filesystem-ftps",
    ":filesystems:filesystem-ftpes",
    ":filesystems:filesystem-sftp",
    // TODO ":filesystems:filesystem-dropbox",
    // TODO ":filesystems:filesystem-googledrive",
)

include(
    ":editorkit:editorkit",
    ":editorkit:language-base",
    ":editorkit:language-actionscript",
    ":editorkit:language-c",
    ":editorkit:language-cpp",
    ":editorkit:language-csharp",
    ":editorkit:language-css",
    ":editorkit:language-fortran",
    ":editorkit:language-go",
    ":editorkit:language-groovy",
    ":editorkit:language-html",
    ":editorkit:language-ini",
    ":editorkit:language-java",
    ":editorkit:language-javascript",
    ":editorkit:language-json",
    ":editorkit:language-julia",
    ":editorkit:language-kotlin",
    ":editorkit:language-latex",
    ":editorkit:language-lisp",
    ":editorkit:language-lua",
    ":editorkit:language-markdown",
    ":editorkit:language-php",
    ":editorkit:language-plaintext",
    ":editorkit:language-python",
    ":editorkit:language-ruby",
    ":editorkit:language-rust",
    ":editorkit:language-shell",
    ":editorkit:language-smali",
    ":editorkit:language-sql",
    ":editorkit:language-toml",
    ":editorkit:language-typescript",
    ":editorkit:language-visualbasic",
    ":editorkit:language-xml",
    ":editorkit:language-yaml",
)