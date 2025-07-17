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
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.assetSpi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.jsonLdLib)
    api(libs.edc.jsonLdSpi)
    api(libs.edc.oauth2Spi)
    api(libs.edc.policyModel)
    api(libs.edc.transformSpi)

    api(project(":ce:libs:api"))
    api(project(":ce:libs:jsonld-lib"))
    api(project(":ce:libs:runtime-os-lib"))

    implementation(libs.apache.commonsLang)
    implementation(libs.apache.commonsCollections)
    implementation(libs.flexmark.all)
    implementation(libs.okhttp.okhttp)
    implementation(libs.jakarta.rsApi)

    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testImplementation(project(":ce:libs:runtime-test-lib"))
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junitJupiter)
    testImplementation(libs.jackson.jsr310)
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
