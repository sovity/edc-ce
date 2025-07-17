/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge

import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.tractusx.edc.agreements.retirement.spi.store.AgreementsRetirementStore

@Provides(AgreementsRetirementStore::class)
class TractusSovityContractTerminationBridgeExtension : ServiceExtension {

    @Inject
    lateinit var dslContextFactory: DslContextFactory

    override fun initialize(context: ServiceExtensionContext) {
        val agreementRetirementStore = TractusRetirementToSovityTerminationAgreementsRetirementStore(dslContextFactory)
        context.registerService(AgreementsRetirementStore::class.java, agreementRetirementStore)
    }
}
