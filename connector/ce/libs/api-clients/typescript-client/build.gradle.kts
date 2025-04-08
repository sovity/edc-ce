/*
 * Copyright 2025 sovity GmbH
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
 */
plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm") apply false
    id("org.jetbrains.kotlin.plugin.lombok") apply false
    alias(libs.plugins.openapi.generator)
}

val openapiFile = "sovity-edc-api-wrapper.yaml"
val outputDirectory = buildFile.parentFile.resolve("src/generated").normalize()

// By using a separate configuration we can skip having the Extension Jar in our runtime classpath
val openapiYaml = configurations.create("openapiGenerator")

dependencies {
    // We only need the openapi.yaml file from this dependency
    openapiYaml(project(":ce:libs:api")) {
        isTransitive = false
    }
}

// Extract the openapi file from the JAR
val buildDirectory = layout.buildDirectory.get().asFile
val extractOpenapiYaml by tasks.registering(Copy::class) {
    doNotTrackState("Idk why this is required")
    dependsOn(openapiYaml)
    into(buildDirectory)
    from(zipTree(openapiYaml.singleFile)) {
        include(openapiFile)
    }
}

val tsClientClean = tasks.create("openApiClean") {
    doLast {
        project.delete(fileTree(outputDirectory).exclude("**/.gitignore"))
    }
}

fun Task.cacheTask() {
    outputs.cacheIf { true }
    inputs.file(buildDirectory.resolve(openapiFile))
    outputs.dir(outputDirectory)
}

tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    cacheTask()

    inputSpec.set(buildDirectory.resolve(openapiFile).toString())
    outputDir.set(outputDirectory.toString())

    dependsOn(tsClientClean)
    dependsOn(extractOpenapiYaml)
    generatorName = "typescript-fetch"
    configOptions = mutableMapOf(
        "supportsES6" to "true",
        "npmVersion" to libs.versions.npmVersion.get(),
        "typescriptThreePlus" to "true",
    )

    validateSpec = false

    // replace backslashes with slashes due to a windows problem where the path must be also a URL
    inputSpec = buildDirectory.resolve(openapiFile).toString().replace("\\", "/")
    outputDir = outputDirectory.toString()

    doLast {
        outputDirectory.resolve("src/generated").renameTo(outputDirectory)
        project.delete(outputDirectory.resolve(".openapi-generator-ignore"))
        project.delete(outputDirectory.resolve(".openapi-generator"))

        // Fixes TS7053: Element implicitly has an 'any' type because expression of type '"..."' can't be used to index type '{}'.
        fileTree("src/generated")
            .apply { include("**/*.ts") }
            .forEach { file ->
                val content = file.readText()
                val newContent = content
                    .replace(": object", ": any")
                    .replace("/* tslint:disable */\n", "")
                file.writeText(newContent)
            }
    }
}

val uiLibDirectory = project.rootProject.projectDir.resolve("../frontend/src/lib/api/client")
tasks.create("copyUiLib") {
    dependsOn("openApiGenerate")
    doLast {
        project.delete(fileTree(uiLibDirectory).exclude("**/.gitkeep", "**/.gitignore"))
        project.copy {
            from("src")
            exclude("vite-env.d.ts")
            into(uiLibDirectory)
        }
    }
}

tasks.clean {
    dependsOn(tsClientClean)
}
