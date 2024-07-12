/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.e2e.utils.AwaitNegotiationPolicy;
import de.sovity.edc.e2e.utils.Consumer;
import de.sovity.edc.e2e.utils.E2eScenario;
import de.sovity.edc.e2e.utils.E2eTestExtension;
import de.sovity.edc.e2e.utils.Provider;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;

@ExtendWith(E2eTestExtension.class)
class DataSourceQueryParamsTest {

    private MockDataAddressRemote dataAddress;
    private final String encodedParam = "a=%25"; // Unencoded param "a=%"
    private final String dataOfferId = "my-data-offer-2023-11";

    @BeforeEach
    void setup(@Provider ConnectorConfig providerConfig) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConfig.getDefaultEndpoint());
    }

    @Test
    void testDirectQuerying() {
        // arrange
        var expected = "a=%";
        // will be encoded in assertResponseContent before request
        var queryParams = new HashMap<String, String>();
        queryParams.put("a", "%");

        // act
        // assert
        validateDataTransferred(dataAddress.getDataSourceQueryParamsUrl(), queryParams, expected);
    }

    /**
     * This test will fail as soon as the handling of query parameters is fixed in the EDC project
     */
    @DisabledOnGithub
    @Test
    void testQueryParamsDoubleEncoded(E2eScenario scenario, @Consumer EdcClient consumerClient) {

        // arrange
        val assetId = "asset-1";
        scenario.createAsset(
            assetId,
            UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceQueryParamsUrl())
                .queryString(encodedParam)
                .build());
        scenario.createContractDefinition(assetId);

        // act
        val negotiation = scenario.negotiateAsset(assetId, AwaitNegotiationPolicy.AWAIT);
        initiateTransfer(consumerClient, negotiation);


        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), encodedParam);
    }

    private void initiateTransfer(EdcClient consumerClient, UiContractNegotiation negotiation) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .dataSinkProperties(dataAddress.getDataSinkProperties())
            .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }
}
