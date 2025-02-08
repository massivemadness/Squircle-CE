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

package com.blacksquircle.ui

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

/**
 * ./gradlew publishReleasePublicationToSonatypeRepository --no-parallel
 */
class PublishModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
            }

            val publishModule = extensions.create("publishModule", PublishModuleExtension::class.java)
            val publishing = extensions["publishing"] as PublishingExtension
            val properties = Properties().apply {
                val localFile = rootProject.file("local.properties")
                if (localFile.exists()) {
                    load(localFile.inputStream())
                }
            }
            extra["signing.keyId"] = properties.getProperty("signing.keyId")
            extra["signing.password"] = properties.getProperty("signing.password")
            extra["signing.secretKeyRingFile"] = properties.getProperty("signing.secretKeyRingFile")
            extra["ossrhUsername"] = properties.getProperty("ossrhUsername")
            extra["ossrhPassword"] = properties.getProperty("ossrhPassword")

            group = publishModule.libraryGroup
            version = publishModule.libraryVersion

            configure<PublishingExtension> {
                afterEvaluate {
                    repositories {
                        maven {
                            name = "sonatype"
                            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                            credentials {
                                username = properties.getProperty("ossrhUsername")
                                password = properties.getProperty("ossrhPassword")
                            }
                        }
                    }
                    publications {
                        create("release", MavenPublication::class.java) {
                            groupId = publishModule.libraryGroup
                            artifactId = publishModule.libraryArtifact
                            version = publishModule.libraryVersion

                            if (plugins.hasPlugin("com.android.library")) {
                                from(components["release"])
                            } else {
                                from(components["java"])
                            }

                            pom {
                                name.set(publishModule.libraryArtifact)
                                description.set("EditorKit is a multi-language code editor for Android.")
                                url.set("https://github.com/massivemadness/EditorKit")
                                licenses {
                                    license {
                                        name.set("Apache 2.0 License")
                                        url.set("https://github.com/massivemadness/EditorKit/blob/master/LICENSE")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set("massivemadness")
                                        name.set("Dmitrii Rubtsov")
                                        email.set("dm.mironov01@gmail.com")
                                    }
                                }
                                scm {
                                    connection.set("scm:git:github.com/massivemadness/EditorKit.git")
                                    developerConnection.set("scm:git:ssh://github.com/massivemadness/EditorKit.git")
                                    url.set("https://github.com/massivemadness/EditorKit/tree/master")
                                }
                            }
                        }
                    }
                }
            }
            configure<SigningExtension> {
                sign(publishing.publications)
            }
        }
    }
}