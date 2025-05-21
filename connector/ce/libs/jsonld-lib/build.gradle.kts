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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.jakarta.json)
    api(libs.apicatalog.titaniumJsonLd)

    implementation(libs.apache.commonsLang)
    implementation(libs.apache.commonsCollections)
    implementation(libs.apache.commonsIo)

    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junitJupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityCeGroupName.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = libs.versions.javaForClients.get()
    targetCompatibility = libs.versions.javaForClients.get()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.javaForClients.get())
        compilerOptions.javaParameters = true
    }
}
