/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset;

import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService;
import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.ce.db.jooq.Tables;
import de.sovity.edc.ce.libs.mappers.AssetMapper;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AssetApiService {
    private final AssetService assetService;
    private final AssetMapper assetMapper;
    private final AssetIdValidator assetIdValidator;
    private final SelfDescriptionService selfDescriptionService;

    public List<UiAsset> getAssets() {
        var assets = getAllAssets();
        var connectorEndpoint = selfDescriptionService.getConnectorEndpoint();
        var participantId = selfDescriptionService.getParticipantId();
        return assets.stream().sorted(Comparator.comparing(Asset::getCreatedAt).reversed())
            .map(asset -> assetMapper.buildUiAsset(asset, connectorEndpoint, participantId))
            .toList();
    }

    @NotNull
    public IdResponseDto createAsset(UiAssetCreateRequest request) {
        assetIdValidator.assertValid(request.getId());
        var organizationName = selfDescriptionService.getCuratorName();
        val asset = assetMapper.buildAsset(request, organizationName);
        val createdAsset = assetService.create(asset).orElseThrow(ServiceException::new);
        return new IdResponseDto(createdAsset.getId());
    }

    public IdResponseDto editAsset(String assetId, UiAssetEditRequest request) {
        val foundAsset = assetService.findById(assetId);
        Objects.requireNonNull(foundAsset, "Asset with ID %s not found".formatted(assetId));
        val editedAsset = assetMapper.editAsset(foundAsset, request);
        val updatedAsset = assetService.update(editedAsset).orElseThrow(ServiceException::new);
        assetService.update(editedAsset);
        return new IdResponseDto(updatedAsset.getId());
    }

    @NotNull
    public IdResponseDto deleteAsset(String assetId) {
        var response = assetService.delete(assetId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<Asset> getAllAssets() {
        return assetService.search(QuerySpec.max()).orElseThrow(ServiceException::new);
    }

    public boolean assetExists(DSLContext dsl, String assetId) {
        val a = Tables.EDC_ASSET;
        return dsl.selectCount()
            .from(a)
            .where(a.ASSET_ID.eq(assetId))
            .fetchSingleInto(Integer.class) > 0;
    }
}
