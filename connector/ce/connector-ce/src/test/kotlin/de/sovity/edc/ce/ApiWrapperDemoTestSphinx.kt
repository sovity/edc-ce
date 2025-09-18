/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.RegisterExtension

// Credentials can be found in Bitwarden, search for "EDC sphin-X Credentials"
@EnabledIfEnvironmentVariable(
    named = "CI_SPHINX_ENABLED",
    matches = "true",
    disabledReason = "sphin-X CI tests have been disabled due to missing env configuration"
)
class ApiWrapperDemoTestSphinx : ApiWrapperDemoTestBase() {
    companion object {
        @RegisterExtension
        val extension = IntegrationTest2xSetupsCe.ceSphinxViaEnv()
    }
}
