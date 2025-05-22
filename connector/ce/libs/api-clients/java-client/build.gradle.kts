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
    alias(libs.plugins.openapi.generator)
}

lombok {
    version = libs.versions.lombok.get()
}

// By using a separate configuration we can skip having the Extension Jar in our runtime classpath
val openapiYaml = configurations.create("openapiGenerator")
val buildDirectory = layout.buildDirectory.get().asFile

dependencies {
    // We only need the openapi.yaml file from this dependency
    openapiYaml(project(":ce:libs:api")) {
        isTransitive = false
    }

    // Generated Client's Dependencies
    implementation(libs.swagger.annotations)
    implementation(libs.findbugs.jsr305)
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.gson)
    implementation(libs.gsonFire)
    implementation(libs.openapi.jacksonDatabindNullable)
    implementation(libs.apache.commonsLang)
    implementation(libs.jakarta.annotationApi)
    api(project(":ce:libs:jsonld-lib"))
}

// Extract the openapi file from the JAR
val openapiFile = "sovity-edc-api-wrapper.yaml"
val extractOpenapiYaml by tasks.registering(Copy::class) {
    doNotTrackState("Idk why this is required")
    dependsOn(openapiYaml)
    into(buildDirectory)
    from(zipTree(openapiYaml.singleFile)) {
        include(openapiFile)
    }
}

tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    dependsOn(extractOpenapiYaml)
    generatorName.set("java")
    configOptions.set(
        mutableMapOf(
            "invokerPackage" to "de.sovity.edc.client.gen",
            "apiPackage" to "de.sovity.edc.client.gen.api",
            "modelPackage" to "de.sovity.edc.client.gen.model",
            "caseInsensitiveResponseHeaders" to "true",
            "additionalModelTypeAnnotations" to listOf(
                "@lombok.AllArgsConstructor",
                "@lombok.Builder",
                "@SuppressWarnings(\"all\")"
            ).joinToString("\n"),
            "annotationLibrary" to "swagger1",
            "hideGenerationTimestamp" to "true",
            "useRuntimeException" to "true",
        )
    )
    validateSpec = false

    // replace backslashes with slashes due to a windows problem where the path must be also a URL
    inputSpec = "${buildDirectory.toString().replace("\\", "/")}/${openapiFile}"
    outputDir = "${buildDirectory}/generated/client-project"
}

val postprocessGeneratedClient by tasks.registering(Copy::class) {
    dependsOn("openApiGenerate")
    from("${buildDirectory}/generated/client-project/src/main/java")

    // @lombok.Builder clashes with the following generated model file.
    // It is the base class for OAS3 polymorphism via allOf/anyOf, which we won't use anyway.
    exclude("**/AbstractOpenApiSchema.java")

    // The Jax-RS dependency suggested by the generated project was causing issues with quarkus.
    // It was again only required for the polymorphism, which we won't use anyway.
    filter { if (it == "import javax.ws.rs.core.GenericType;") "" else it }

    // Add Builder.Defaults to fields with initializers
    val regex = "private (.*)=(.*)".toRegex()
    filesMatching("de/sovity/edc/client/gen/model/**/*.java") {
        filter { it.replace(regex, "  @lombok.Builder.Default\n  private $1 = $2") }
    }

    into("${buildDirectory}/generated/sources/openapi/java/main")
}
sourceSets["main"].java.srcDir("${buildDirectory}/generated/sources/openapi/java/main")

tasks.configureEach {
    if (name == "generateEffectiveLombokConfig") {
        dependsOn("postprocessGeneratedClient")
    }
}

tasks.test {
    onlyIf { false }
}

tasks.withType<JavaCompile> {
    dependsOn("openApiGenerate")
    dependsOn("delombok")
    dependsOn("postprocessGeneratedClient")
    options.isWarnings = false

    sourceCompatibility = libs.versions.javaForClients.get()
    targetCompatibility = libs.versions.javaForClients.get()
}

group = libs.versions.sovityCeGroupName.get()

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(postprocessGeneratedClient)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifactId = "client"
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}
