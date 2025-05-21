/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.catena.edrs

import de.sovity.edc.ce.api.common.model.EdrDto
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrApiService
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrTransferProcessUtils
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.JwtUtils
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.JsonLdUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.JsonObject
import org.eclipse.edc.connector.controlplane.services.spi.contractnegotiation.ContractNegotiationService
import org.eclipse.edc.edr.spi.store.EndpointDataReferenceStore
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.transform.spi.TypeTransformerRegistry
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry
import org.eclipse.tractusx.edc.api.edr.v2.EdrCacheApiV2Controller
import org.eclipse.tractusx.edc.edr.spi.service.EdrService

@Service
class EdrApiServiceV2CatenaImpl(
    private val endpointDataReferenceStore: EndpointDataReferenceStore,
    typeTransformerRegistry: TypeTransformerRegistry,
    validator: JsonObjectValidatorRegistry,
    monitor: Monitor,
    edrService: EdrService,
    contractNegotiationService: ContractNegotiationService,
    private val jwtUtils: JwtUtils,
    private val edrTransferProcessUtils: EdrTransferProcessUtils
) : EdrApiService {
    // Tractus V2 EDR API
    private val edrCacheApiController = EdrCacheApiV2Controller(
        endpointDataReferenceStore,
        typeTransformerRegistry.forContext("management-api"),
        validator,
        monitor,
        edrService,
        contractNegotiationService
    )

    override fun getTransferProcessEdr(transferId: String): EdrDto {
        val jsonObject = edrCacheApiController.getEdrEntryDataAddress(transferId, true)
        return v2DataAddressToEdrDto(jsonObject)
    }

    override fun terminateTransferProcess(transferId: String): IdResponseDto {
        // try delete EDR and ignore failures
        endpointDataReferenceStore.delete(transferId)

        // terminate transfer
        edrTransferProcessUtils.terminateTransfer(transferId, "Transfer has been manually terminated.")

        return IdResponseDto.builder()
            .id(transferId)
            .build()
    }

    private fun v2DataAddressToEdrDto(jsonObject: JsonObject): EdrDto {
        val baseUrl = JsonLdUtils.string(jsonObject, Prop.Edc.ENDPOINT)
        val token = JsonLdUtils.string(jsonObject, Prop.Edc.AUTHORIZATION)

        require(!baseUrl.isNullOrBlank()) { "Failed retrieving EDR. Base URL was blank: $jsonObject" }
        val expiresAt = jwtUtils.getExpirationDateOrNull(token)
        require(expiresAt != null) { "Claim 'exp' not filled by EDR v2 API. Please adjust code" }

        return EdrDto.builder()
            .baseUrl(baseUrl)
            .authorizationHeaderValue(token)
            .expiresAt(expiresAt)
            .build()
    }
}
