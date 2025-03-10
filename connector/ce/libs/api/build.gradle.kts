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
    `maven-publish`

    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.swagger.plugin)  //./gradlew clean resolve
}

lombok {
    version = libs.versions.lombok.get()
}

dependencies {
    implementation(project(":ce:libs:jsonld-lib"))
    implementation(libs.apache.commonsLang)
    implementation(libs.hibernate.validation)
    implementation(libs.jakarta.el)
    implementation(libs.jakarta.rsApi)
    implementation(libs.jakarta.servletApi)
    implementation(libs.jakarta.validationApi)
    api(libs.swagger.annotationsJakarta)
    api(libs.swagger.jaxrs2Jakarta)
}

val openapiFileDir = project.layout.buildDirectory.get().asFile
    .resolve("generated/openapi")
    .toString()
val openapiFileFilename = "sovity-edc-api-wrapper.yaml"

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    setDependsOn(listOf("classes"))
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
    openAPI31 = true
}

val openapiFile = "$openapiFileDir/$openapiFileFilename"
val openapiDocsDir = project.rootProject.rootDir.parentFile
    .resolve("docs/api")

val copyOpenapiYamlToDocs by tasks.registering(Copy::class) {
    dependsOn("resolve")
    from(openapiFile)
    into(openapiDocsDir)
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn("resolve")
    from(openapiFileDir) {
        include(openapiFileFilename)
    }
}

tasks.withType<JavaCompile> {
    dependsOn("delombok")
    options.isWarnings = false
}

group = libs.versions.sovityCeGroupName.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
