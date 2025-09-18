/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import org.junit.jupiter.api.extension.RegisterExtension

/**
 * This test is the CE because it is referenced from documentation
 */
class ApiWrapperDemoTestIamMock : ApiWrapperDemoTestBase() {
    companion object {
        @RegisterExtension
        val extension = IntegrationTest2xSetupsCe.ceSovityIamMock()
    }
}
