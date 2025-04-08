/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("plugin.lombok") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.taskinfo)
}

subprojects {
    val projectsWithoutKotlin = listOf(
        ":ce:libs:api",
        ":ce:libs:api-clients:java-client",
        ":ce:libs:api-clients:typescript-client",
    ).map { project(it).name }
    if (!projectsWithoutKotlin.contains(project.name)) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
    }

    val libs = rootProject.libs

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = libs.versions.java.get()
        targetCompatibility = libs.versions.java.get()
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
            compilerOptions.javaParameters = true
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()

        // Enable discovery of extensions via META-INF/services/org.junit.jupiter.api.extension.Extension
        // Used for our JUnit 5 Test Sharding
        jvmArgs = listOf("-Djunit.jupiter.extensions.autodetection.enabled=true")

        testLogging {
            events = setOf(TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            sourceSets.configureEach {
                if (name == "main") {
                    java.srcDirs("src/main/java", "src/main/kotlin")
                } else if (name == "test") {
                    java.srcDirs("src/test/java", "src/test/kotlin")
                }
            }
        }
    }

    plugins.withType<KotlinPlatformJvmPlugin> {
        configure<KotlinProjectExtension> {
            sourceSets.configureEach {
                if (name == "main") {
                    kotlin.srcDirs("src/main/kotlin")
                } else if (name == "test") {
                    kotlin.srcDirs("src/test/kotlin")
                }
            }
        }
    }


    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }

    repositories {
        maven {
            url =
                uri("https://pkgs.dev.azure.com/sovity/41799556-91c8-4df6-8ddb-4471d6f15953/_packaging/core-edc/maven/v1")
            name = "AzureRepo"
        }
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            name = "GitHub-TractusX-EDC"
            url = uri("https://maven.pkg.github.com/eclipse-tractusx/tractusx-edc")
            withGitHubCredentials()
        }
    }
}

fun MavenArtifactRepository.withGitHubCredentials() {
    val gitHubUser = System.getenv("USERNAME")
        ?: project.findProperty("gpr.user") as String?
    val gitHubToken = System.getenv("TOKEN")
        ?: project.findProperty("gpr.key") as String?

    if (gitHubUser.isNullOrBlank() || gitHubToken.isNullOrBlank()) {
        @Suppress("MaxLineLength")
        error("Need Gradle Properties 'gpr.user' and 'gpr.key' or environment variables 'USERNAME' and 'TOKEN' with a GitHub PAT with 'Repository read access' to access the GitHub Maven Repository.")
    }

    credentials {
        username = gitHubUser
        password = gitHubToken
    }
}

