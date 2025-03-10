/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

import de.sovity.edc.runtime.modules.model.EdcModule

object SimplePolicyCreatorModule {
    fun instance() = EdcModule(
        name = "simple-policy-creator",
        documentation = "Helps you extend the EDC with custom policies that support most operators"
    ).apply {
        serviceExtensions(SimplePolicyCreatorExtension::class.java)
    }
}
