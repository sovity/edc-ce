/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
plugins {
    `java-library`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":ce:libs:api-clients:java-client"))
    api(project(":ce:libs:jsonld-lib"))
    api(project(":ce:libs:runtime-lib"))
    api(project(":ce:utils:dependency-bundles-ce"))

    api(libs.edc.contractSpi)
    api(libs.edc.jsonLdSpi)
    api(libs.edc.jsonLdLib)
    api(libs.edc.junit)

    api(libs.apache.commonsLang)

    api(libs.bundles.testUtils)
    api(libs.mockserver.netty)
    api(libs.awaitility.java)
}
