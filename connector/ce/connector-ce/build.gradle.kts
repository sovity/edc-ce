/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
plugins {
    `java-library`
    alias(libs.plugins.taskinfo)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    // Core EDC Extensions
    api(project(":ce:utils:dependency-bundles-ce"))

    // Core EDC SPIs
    api(libs.edc.assetSpi)
    api(libs.edc.authSpi)
    api(libs.edc.contractSpi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.dspHttpSpi)
    api(libs.edc.httpLib)
    api(libs.edc.httpSpi)
    api(libs.edc.keysLib)
    api(libs.edc.oauth2Spi)
    api(libs.edc.policyEngineSpi)
    api(libs.edc.transferSpi)

    // Core EDC LIBs
    api(libs.edc.bootLib)
    api(libs.edc.transformLib)

    api(project(":ce:libs:api"))
    api(project(":ce:libs:jsonld-lib"))
    api(project(":ce:libs:mappers-lib"))
    api(project(":ce:libs:runtime-lib"))
    api(project(":ce:utils:db-schema-ce"))
    api(project(":ce:utils:versions-ce"))

    api(libs.apache.commonsIo)
    api(libs.apache.commonsLang)
    api(libs.hibernate.validation)
    api(libs.jackson.jsr310)
    api(libs.jakarta.el)
    api(libs.jakarta.rsApi)
    api(libs.jakarta.validationApi)
    api(libs.jooq.jooq)

    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)

    testImplementation(project(":ce:libs:api-clients:java-client"))
    testImplementation(project(":ce:libs:runtime-test-lib"))
    testImplementation(libs.bundles.testUtils)
    testRuntimeOnly(libs.junit.engine)
}

