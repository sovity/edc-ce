/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.ce.api.common.model.EdrDto
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.JsonLdUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.JsonObject
import org.eclipse.edc.connector.controlplane.api.management.edr.BaseEdrCacheApiController
import org.eclipse.edc.edr.spi.store.EndpointDataReferenceStore
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.transform.spi.TypeTransformerRegistry
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry

@Service
class EdrApiServiceV3GenericImpl(
    private val edrTransferProcessUtils: EdrTransferProcessUtils,
    private val endpointDataReferenceStore: EndpointDataReferenceStore,
    typeTransformerRegistry: TypeTransformerRegistry,
    validator: JsonObjectValidatorRegistry,
    monitor: Monitor,
    private val jwtUtils: JwtUtils
) : EdrApiService {
    // Eclipse EDC V3 EDR API
    private val edrCacheApiController = BaseEdrCacheApiController(
        endpointDataReferenceStore,
        typeTransformerRegistry.forContext("management-api"),
        validator,
        monitor,
    )

    override fun getTransferProcessEdr(transferId: String): EdrDto {
        val jsonObject = edrCacheApiController.getEdrEntryDataAddress(transferId)
        return v3DataAddressToEdrDto(jsonObject)
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

    private fun v3DataAddressToEdrDto(jsonObject: JsonObject): EdrDto {
        val baseUrl = JsonLdUtils.string(jsonObject, Prop.Edc.ENDPOINT)
        require(!baseUrl.isNullOrBlank()) { "Failed retrieving EDR. Base URL was blank: $jsonObject" }

        val token = JsonLdUtils.string(jsonObject, Prop.Edc.AUTHORIZATION)
        val expiresAt = jwtUtils.getExpirationDateOrNull(token)
        require(expiresAt == null) { "Migration note: EDR v3 now sets 'exp' claim, please adjust code" }

        return EdrDto.builder()
            .baseUrl(baseUrl)
            .authorizationHeaderValue(token)
            .expiresAt(expiresAt)
            .build()
    }
}
