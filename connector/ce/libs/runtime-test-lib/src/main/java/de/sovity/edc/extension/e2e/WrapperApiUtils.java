/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e;

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
