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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceOnRequest;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import jakarta.ws.rs.HttpMethod;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

@ExtendWith(E2eTestExtension.class)
class PlaceholderDataSourceExtensionTest {

    @SneakyThrows
    @Test
    void shouldAccessDummyEndpoint(
        E2eScenario scenario,
        ClientAndServer clientAndServer,
        @Provider EdcClient providerClient
    ) {
        // arrange

        val assetId = "asset-1";
        val email = "contact@example.com";
        val subject = "Data request";
        providerClient.uiApi().createAsset(
            UiAssetCreateRequest.builder()
                .dataSource(UiDataSource.builder()
                    .type(DataSourceType.ON_REQUEST)
                    .onRequest(UiDataSourceOnRequest.builder()
                        .contactEmail(email)
                        .contactPreferredEmailSubject(subject)
                        .build())
                    .build())
                .id(assetId)
                .build()
        );

        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        val accessed = new AtomicReference<>("Not accessed.");
        val destinationPath = "/foo/bar";
        val destinationUrl = "http://localhost:" + clientAndServer.getPort() + destinationPath;

        clientAndServer.when(HttpRequest.request().withMethod(HttpMethod.POST))
            .respond((it) -> {
                accessed.set(it.getBodyAsString());
                return HttpResponse.response().withStatusCode(HttpStatusCode.OK_200.code());
            });

        scenario.transferAndAwait(InitiateTransferRequest.builder()
            .contractAgreementId(negotiation.getContractAgreementId())
            .dataSinkProperties(Map.of(
                EDC_NAMESPACE + "baseUrl", destinationUrl,
                EDC_NAMESPACE + "method", HttpMethod.POST,
                EDC_NAMESPACE + "type", "HttpData"
            ))
            .build());

        // assert
        assertThat(new String(Base64.getDecoder().decode(accessed.get())))
            .contains("This is not real data.")
            .contains(email)
            .contains(subject);
    }
}
