/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.policies

import de.sovity.edc.ce.modules.dataspaces.sovity.policies.evaluation_time.EvaluationTimePolicyExtension
import de.sovity.edc.ce.modules.dataspaces.sovity.policies.referring_connector.ReferringConnectorPolicyExtension
import de.sovity.edc.runtime.modules.model.EdcModule

object SovityPoliciesModule {
    fun instance() = EdcModule(
        name = "sovity-policies",
        documentation = "Policies for sovity dataspaces"
    ).apply {
        serviceExtensions(
            // Policies
            EvaluationTimePolicyExtension::class.java,
            ReferringConnectorPolicyExtension::class.java,
        )
    }
}
