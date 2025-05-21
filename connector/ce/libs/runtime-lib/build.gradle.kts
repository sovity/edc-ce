/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":ce:libs:runtime-os-lib"))
    api(project(":ce:utils:dependency-bundles-ce"))
    api(libs.edc.boot)

    // Logging
    api(libs.log4j.core)
    api(libs.log4j.jul)
    api(libs.log4j.layoutTemplateJson)

    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testImplementation(libs.bundles.testUtils)
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

