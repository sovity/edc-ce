/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_detail_page.services

import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.ui.pages.asset.DataAddressBuilder
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.ContractNegotiationUtils
import de.sovity.edc.ce.api.utils.jooq.EdcJsonUtils
import de.sovity.edc.ce.libs.mappers.AssetMapper
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation

@Service
class ContractDetailPageAssetBuilder(
    private val assetMapper: AssetMapper,
    private val jsonUtils: EdcJsonUtils,
    private val contractNegotiationUtils: ContractNegotiationUtils,
    private val dataAddressBuilder: DataAddressBuilder,
) {
    fun buildUiAsset(agreement: ContractDetailPageRs): UiAsset {
        val negotiationType = ContractNegotiation.Type.valueOf(agreement.negotiationType)
        val asset = if (agreement.assetCreatedAt != null) {
            Asset.Builder.newInstance()
                .id(agreement.assetId)
                .createdAt(agreement.assetCreatedAt)
                .properties(agreement.assetProperties?.let { jsonUtils.parseMap(it) })
                .privateProperties(agreement.assetPrivateProperties?.let { jsonUtils.parseMap(it) })
                .dataAddress(
                    agreement.assetDataAddress?.let {
                        dataAddressBuilder.buildDataAddress(it)
                    }
                )
                .build()
        } else {
            Asset.Builder.newInstance().id(agreement.assetId).build()
        }
        val assetParticipantId: String = contractNegotiationUtils.getProviderParticipantId(
            negotiationType = negotiationType,
            counterPartyId = agreement.negotiationCounterPartyId
        )
        val assetConnectorEndpoint: String = contractNegotiationUtils.getProviderConnectorEndpoint(
            negotiationType = negotiationType,
            counterPartyAddress = agreement.negotiationCounterPartyAddress
        )
        return assetMapper.buildUiAsset(asset, assetConnectorEndpoint, assetParticipantId)
    }
}
