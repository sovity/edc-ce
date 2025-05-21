/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object ContractTerminationModule {
    fun instance() = EdcModule(
        name = "contract-termination",
        documentation = "Contract Termination for sovity dataspaces"
    ).apply {
        serviceExtensions(ContractTerminationExtension::class.java)

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_CONTRACT_TERMINATION_THREAD_POOL_SIZE
        ) {
            defaultValue("10")
        }
    }
}
