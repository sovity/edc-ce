/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services

import de.sovity.edc.ce.api.common.model.UiInitiateTransferRequest
import de.sovity.edc.ce.api.common.model.UiInitiateTransferType
import de.sovity.edc.ce.api.ui.model.InitiateCustomTransferRequest
import de.sovity.edc.ce.api.ui.model.InitiateTransferRequest
import de.sovity.edc.ce.api.ui.pages.asset.DataAddressBuilder
import de.sovity.edc.ce.api.utils.ServiceException
import de.sovity.edc.ce.libs.mappers.asset.utils.EdcPropertyUtils
import de.sovity.edc.ce.libs.mappers.dataaddress.InitiateTransferRequestMapper
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.JsonUtils
import de.sovity.edc.utils.jsonld.JsonLdUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol
import org.eclipse.edc.transform.spi.TypeTransformerRegistry
import org.jooq.DSLContext

@Service
class TransferRequestBuilder(
    private val initiateTransferRequestMapper: InitiateTransferRequestMapper,
    private val edcPropertyUtils: EdcPropertyUtils,
    private val typeTransformerRegistry: TypeTransformerRegistry,
    private val callbackAddressMapper: CallbackAddressMapper,
    private val contractNegotiationUtils: ContractNegotiationUtils,
    private val contractAgreementAssetFetcher: ContractAgreementAssetFetcher,
    private val dataAddressBuilder: DataAddressBuilder,
) {

    fun buildTransferRequestV2(
        request: UiInitiateTransferRequest
    ): TransferRequest {
        // decide transfer and data address properties
        val params = initiateTransferRequestMapper.buildInitiateTransferParams(request)

        val negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(request.contractAgreementId)
        assertIsConsuming(negotiation)

        val dataAddress = dataAddressBuilder.buildDataAddress(params.dataSinkProperties)
        val transferProcessProperties = edcPropertyUtils.toMapOfObject(params.transferPrivateProperties)
        val callbackAddresses = callbackAddressMapper.buildCallbackAddresses(request.callbackAddresses)

        return TransferRequest.Builder.newInstance()
            .id(org.eclipse.edc.spi.uuid.UuidGenerator.INSTANCE.generate().toString())
            .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
            .counterPartyAddress(negotiation.counterPartyAddress)
            .contractId(request.contractAgreementId)
            .transferType(params.transferType)
            .dataDestination(dataAddress)
            .privateProperties(transferProcessProperties)
            .callbackAddresses(callbackAddresses)
            .build()
    }

    @Deprecated("")
    fun buildTransferRequest(
        request: InitiateTransferRequest
    ): TransferRequest {
        return buildTransferRequestV2(
            UiInitiateTransferRequest.builder()
                .type(UiInitiateTransferType.CUSTOM)
                .contractAgreementId(request.contractAgreementId)
                .customTransferType(request.transferType)
                .customTransferPrivateProperties(request.transferProcessProperties)
                .customDataSinkProperties(request.dataSinkProperties)
                .build()
        )
    }

    fun buildCustomTransferRequest(
        dsl: DSLContext,
        request: InitiateCustomTransferRequest
    ): TransferRequest {
        val contractId = request.contractAgreementId
        val agreementAssetId = contractAgreementAssetFetcher.fetchAssetId(dsl, contractId)
        val negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(request.contractAgreementId)
        assertIsConsuming(negotiation)

        // Parse Transfer Process JSON-LD
        var requestJsonLd = JsonUtils.parseJsonObj(request.transferProcessRequestJsonLd)

        // Expand JSON-LD Property names
        requestJsonLd = Json.createObjectBuilder(requestJsonLd)
            .add(Prop.TYPE, Prop.Edc.TYPE_TRANSFER_REQUEST)
            .add(
                Prop.CONTEXT, Json.createObjectBuilder(JsonLdUtils.`object`(requestJsonLd, Prop.CONTEXT))
                    .add(Prop.Edc.CTX_ALIAS, Prop.Edc.CTX)
            )
            .build()
        requestJsonLd = JsonLdUtils.expandKeysOnly(requestJsonLd)

        // Add missing properties
        requestJsonLd = Json.createObjectBuilder(requestJsonLd)
            .add(Prop.TYPE, Prop.Edc.TYPE_TRANSFER_REQUEST)
            .add(Prop.Edc.ASSET_ID, agreementAssetId)
            .add(Prop.Edc.CONTRACT_ID, contractId)
            .add(Prop.Edc.CONNECTOR_ID, negotiation.counterPartyId)
            .add(Prop.Edc.COUNTER_PARTY_ADDRESS, negotiation.counterPartyAddress)
            .build()

        return typeTransformerRegistry.forContext("management-api").transform(
            requestJsonLd,
            TransferRequest::class.java
        ).orElseThrow { ServiceException(it) }
    }

    private fun assertIsConsuming(negotiation: ContractNegotiation) {
        require(negotiation.type == ContractNegotiation.Type.CONSUMER) {
            "Agreement is not a consuming agreement."
        }
    }
}
