package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ParameterizationCompatibilityUtils;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
                        "https://w3id.org/edc/v0.0.1/ns/pathSegments", "my-endpoint",
                        "https://w3id.org/edc/v0.0.1/ns/method", "METHOD",
                        "https://w3id.org/edc/v0.0.1/ns/queryParams", "queryParams",
                        "https://w3id.org/edc/v0.0.1/ns/contentType", "mimetype",
                        "https://w3id.org/edc/v0.0.1/ns/body", "[]"
                )
        );

        final var transformer = createTransferRequestBuilder();

        val ROOT_KEY = "https://sovity.de/workaround/proxy/param/";

        // act
        val actual = transformer.buildCustomTransferRequest(request);

        val workaroundProperties = actual.getDataDestination().getProperties();
        assertThat(workaroundProperties).isNotEmpty();
        assertThat(workaroundProperties.get(ROOT_KEY + "pathSegments")).isEqualTo("my-endpoint");
        assertThat(workaroundProperties.get(ROOT_KEY + "method")).isEqualTo("METHOD");
        assertThat(workaroundProperties.get(ROOT_KEY + "queryParams")).isEqualTo("queryParams");
        assertThat(workaroundProperties.get(ROOT_KEY + "contentType")).isEqualTo("mimetype");
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
