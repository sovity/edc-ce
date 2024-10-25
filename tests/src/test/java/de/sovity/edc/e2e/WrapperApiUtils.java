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
import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.UiAsset;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
public class WrapperApiUtils {

    public static @NotNull List<UiAsset> getAssetsWithId(EdcClient providerClient, String assetId) {
        return providerClient.uiApi()
            .getAssetPage()
            .getAssets()
            .stream()
            .filter(it -> it.getAssetId().equals(assetId))
            .toList();
    }

    public static @NotNull List<ContractDefinitionEntry> getContractDefinitionWithAssetId(
        EdcClient providerClient,
        String assetId
    ) {
        return providerClient.uiApi()
            .getContractDefinitionPage()
            .getContractDefinitions()
            .stream().filter(it -> it.getContractDefinitionId().equals(assetId))
            .toList();
    }

    public static List<ContractDefinitionEntry> getContractDefinitionWithId(EdcClient client, String id) {
        return client.uiApi()
            .getContractDefinitionPage()
            .getContractDefinitions()
            .stream()
            .filter(it -> it.getContractDefinitionId().equals(id))
            .toList();
    }
}
