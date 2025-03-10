/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.cleanup;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.IdResponseDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class JanitorApiWrapper {

    private final Janitor janitor;
    private final EdcClient client;

    public IdResponseDto createAsset(UiAssetCreateRequest uiAssetCreateRequest) {
        val result = client.uiApi().createAsset(uiAssetCreateRequest);
        janitor.afterEach(() -> client.uiApi().deleteAsset(result.getId()));
        return result;
    }

    public IdResponseDto createPolicyDefinitionV2(PolicyDefinitionCreateDto policyDefinition) {
        val result = client.uiApi().createPolicyDefinitionV2(policyDefinition);
        janitor.afterEach(() -> client.uiApi().deletePolicyDefinition(result.getId()));
        return result;
    }

    public IdResponseDto createContractDefinition(ContractDefinitionRequest contractDefinitionRequest) {
        val result = client.uiApi().createContractDefinition(contractDefinitionRequest);
        janitor.afterEach(() -> client.uiApi().deleteContractDefinition(result.getId()));
        return result;
    }
}
