/*
 *  Copyright (c) 2024 sovity GmbH
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

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ParameterizationCompatibilityUtils;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransferRequestBuilderTest {
    @Test
    void ensureThatThePreviousCustomPropertiesAreCopiedOverToTheDataSource() {
        // arrange
        var request = new InitiateTransferRequest(
                "contract-id",
                Map.of(
                        "type", "HttpData",
                        "baseUrl", "http://example.com/segment0"
                ),
                Map.of(
                        EDC_NAMESPACE + "pathSegments", "my-endpoint",
                        EDC_NAMESPACE + "method", "METHOD",
                        EDC_NAMESPACE + "queryParams", "queryParams",
                        EDC_NAMESPACE + "contentType", "mimetype",
                        EDC_NAMESPACE + "body", "[]"
                )
        );

        final var transformer = createTransferRequestBuilder();

        val ROOT_KEY = "https://sovity.de/workaround/proxy/param/";

        // act
        val actual = transformer.buildCustomTransferRequest(request);

        // assert
        val workaroundProperties = actual.getDataDestination().getProperties();
        assertThat(workaroundProperties).isNotEmpty();
        assertThat(workaroundProperties.get(ROOT_KEY + "pathSegments")).isEqualTo("my-endpoint");
        assertThat(workaroundProperties.get(ROOT_KEY + "method")).isEqualTo("METHOD");
        assertThat(workaroundProperties.get(ROOT_KEY + "queryParams")).isEqualTo("queryParams");
        assertThat(workaroundProperties.get(ROOT_KEY + "mediaType")).isEqualTo("mimetype");
        assertThat(workaroundProperties.get(ROOT_KEY + "body")).isEqualTo("[]");
    }

    @NotNull
    private static TransferRequestBuilder createTransferRequestBuilder() {
        val agreement = ContractAgreement.Builder.newInstance()
                .id("contract-agreement-id")
                .assetId("asset-id")
                .providerId("provider-id")
                .consumerId("consumer-id")
                .policy(Policy.Builder.newInstance().build())
                .build();

        val contractAgreementUtils = mock(ContractAgreementUtils.class);
        when(contractAgreementUtils.findByIdOrThrow(any())).thenReturn(agreement);

        val contractNegotiationUtils = mock(ContractNegotiationUtils.class);

        val contractNegotiation = ContractNegotiation.Builder.newInstance()
                .id("contract-negotiation-id")
                .type(ContractNegotiation.Type.CONSUMER)
                .counterPartyId("counter-party-id")
                .counterPartyAddress("counter-party-address")
                .protocol("protocol")
                .contractAgreement(agreement)
                .build();
        when(contractNegotiationUtils.findByContractAgreementIdOrThrow(any())).thenReturn(contractNegotiation);

        val transformer = new TransferRequestBuilder(
                contractAgreementUtils,
                contractNegotiationUtils,
                new EdcPropertyUtils(),
                mock(TypeTransformerRegistry.class),
                new ParameterizationCompatibilityUtils()
        );
        return transformer;
    }
}
