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

package de.sovity.edc.extension.e2e.junit;

import de.sovity.edc.client.EdcClient;
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
        janitor.afterEach(() -> client.uiApi().deleteAsset(result.getId()));
        return result;
    }
}
