/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.dependency_bundles

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.edc.iam.mock.IamMockExtension
import org.eclipse.edc.spi.system.ServiceExtension
import org.junit.jupiter.api.Test

class CeDependencyBundlesTest {
    @Test
    fun testIamMockBundle() {
        val classes = CeDependencyBundles.c2cIamMock.getServices(ServiceExtension::class.java)
        assertThat(classes).containsExactly(IamMockExtension::class.java)
    }
}
