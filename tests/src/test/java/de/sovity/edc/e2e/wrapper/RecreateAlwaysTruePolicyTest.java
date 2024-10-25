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

package de.sovity.edc.e2e.wrapper;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.DataOfferCreationRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.client.gen.model.UiAsset;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceOnRequest;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecreateAlwaysTruePolicyTest {

    @RegisterExtension
    private static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @Test
    void recreateTheAlwaysTruePolicyIfDeleted(@Provider EdcClient providerClient) {
        // arrange
        val assetId = "assetId";
        providerClient.uiApi().deletePolicyDefinition(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID);

        List<PolicyDefinitionDto> withoutDefaultAlwaysTrue =
            providerClient.uiApi()
                .getPolicyDefinitionPage()
                .getPolicies()
                .stream()
                .filter(it -> !it.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID))
                .toList();

        assertThat(withoutDefaultAlwaysTrue).hasSize(0);

        // act
        providerClient.uiApi()
            .createDataOffer(DataOfferCreationRequest.builder()
                .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                    .id(assetId)
                    .dataSource(UiDataSource.builder()
                        .type(DataSourceType.ON_REQUEST)
                        .onRequest(UiDataSourceOnRequest.builder()
                            .contactEmail("foo@example.com")
                            .contactPreferredEmailSubject("Subject")
                            .build())
                        .build())
                    .build())
                .policy(DataOfferCreationRequest.PolicyEnum.PUBLISH_UNRESTRICTED)
                .build());

        // assert
        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            // the asset used for the placeholder contract definition
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        List<PolicyDefinitionDto> policies =
            providerClient.uiApi()
                .getPolicyDefinitionPage()
                .getPolicies()
                .stream()
                .toList();

        assertThat(policies).hasSize(1);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .hasSize(1);
    }

}
