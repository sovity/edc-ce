/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.always_true

import de.sovity.edc.runtime.modules.model.EdcModule

object AlwaysTruePolicyDefinitionModule {
    fun instance() = EdcModule(
        name = "always-true-policy-definition",
        documentation = "Adds the policy definition 'always-true' to the EDC."
    ).apply {
        serviceExtensions(AlwaysTruePolicyDefinitionExtension::class.java)
    }
}
