/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ParameterizationCompatibilityUtils;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateCustomTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TransferRequestBuilder {
    private final ContractAgreementUtils contractAgreementUtils;
    private final ContractNegotiationUtils contractNegotiationUtils;
    private final EdcPropertyUtils edcPropertyUtils;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final ParameterizationCompatibilityUtils parameterizationCompatibilityUtils;

    public TransferRequest buildCustomTransferRequest(
            InitiateTransferRequest request
    ) {
        var contractId = request.getContractAgreementId();
        var agreement = contractAgreementUtils.findByIdOrThrow(contractId);
        var negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(contractId);
        var address = parameterizationCompatibilityUtils.enrich(edcPropertyUtils.buildDataAddress(request.getDataSinkProperties()), request.getTransferProcessProperties());
        assertIsConsuming(negotiation);

        return TransferRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .connectorAddress(negotiation.getCounterPartyAddress())
                .connectorId(negotiation.getCounterPartyId())
                .contractId(contractId)
                .assetId(agreement.getAssetId())
                .dataDestination(address)
                .privateProperties(edcPropertyUtils.toMapOfObject(request.getTransferProcessProperties()))
                .callbackAddresses(List.of())
                .build();
    }

    public TransferRequest buildCustomTransferRequest(
            InitiateCustomTransferRequest request
    ) {
        var contractId = request.getContractAgreementId();
        var agreement = contractAgreementUtils.findByIdOrThrow(contractId);
        var negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(contractId);
        assertIsConsuming(negotiation);

        // Parse Transfer Process JSON-LD
        var requestJsonLd = JsonUtils.parseJsonObj(request.getTransferProcessRequestJsonLd());

        // Expand JSON-LD Property names
        requestJsonLd = Json.createObjectBuilder(requestJsonLd)
                .add(Prop.TYPE, Prop.Edc.TYPE_TRANSFER_REQUEST)
                .add(Prop.CONTEXT, Json.createObjectBuilder(JsonLdUtils.object(requestJsonLd, Prop.CONTEXT))
                        .add(Prop.Edc.CTX_ALIAS, Prop.Edc.CTX))
                .build();
        requestJsonLd = JsonLdUtils.expandKeysOnly(requestJsonLd);

        // Add missing properties
        requestJsonLd = Json.createObjectBuilder(requestJsonLd)
                .add(Prop.TYPE, Prop.Edc.TYPE_TRANSFER_REQUEST)
                .add(Prop.Edc.ASSET_ID, agreement.getAssetId())
                .add(Prop.Edc.CONTRACT_ID, agreement.getId())
                .add(Prop.Edc.CONNECTOR_ID, negotiation.getCounterPartyId())
                .add(Prop.Edc.CONNECTOR_ADDRESS, negotiation.getCounterPartyAddress())
                .build();

        return typeTransformerRegistry.transform(requestJsonLd, TransferRequest.class)
                .orElseThrow(ServiceException::new);
    }

    private void assertIsConsuming(ContractNegotiation negotiation) {
        Validate.isTrue(negotiation.getType() == ContractNegotiation.Type.CONSUMER,
                "Agreement is not a consuming agreement.");
    }
}
