/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_negotiations

import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest
import de.sovity.edc.ce.libs.mappers.PolicyMapper
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer

@Service
class ContractOfferMapper(
    private val policyMapper: PolicyMapper,
) {
    fun buildContractOffer(contractRequest: ContractNegotiationRequest): ContractOffer {
        val policy = policyMapper.buildPolicy(contractRequest.policyJsonLd)

        // Required or Eclipse EDC Validation in DSP panics
        // despite assetId being a field on the ContractOffer
        // despite the catalog not putting it out while policies aren't asset specific
        val policyEdited = policy.toBuilder()
            .target(contractRequest.assetId)
            .assigner(contractRequest.counterPartyId)
            .build()

        return ContractOffer.Builder.newInstance()
            .id(contractRequest.contractOfferId)
            .policy(policyEdited)
            .assetId(contractRequest.assetId)
            .build()
    }
}
