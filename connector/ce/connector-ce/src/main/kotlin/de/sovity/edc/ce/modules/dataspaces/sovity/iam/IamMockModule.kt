/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.iam

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object IamMockModule {

    @JvmStatic
    fun instance() = EdcModule(
        name = "c2c-iam-mock",
        documentation = "Mocked Connector-to-Connector Identity and Access Management for testing and demo purposes"
    ).apply {
        dependencyBundle(CeDependencyBundles.c2cIamMock)
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_PARTICIPANT_ID
        ) {
            required()
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_AGENT_IDENTITY_KEY
        ) {
            defaultValue("client_id")
        }
    }
}
