/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

/**
 * When annotated on a parameter injected by JUnit, in conjunction with
 * [de.sovity.edc.extension.e2e.junit.IntegrationTestCpDpExtension],
 * specifies that the injected instance must come from the Control Plane
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ControlPlane
