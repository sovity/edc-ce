/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditMetadataRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class AssetBuilder {
    private final AssetMapper assetMapper;
    private final EdcPropertyUtils edcPropertyUtils;
    private final AssetIdValidator assetIdValidator;
    private final SelfDescriptionService selfDescriptionService;

    /**
     * Creates an {@link Asset} from a {@link UiAssetCreateRequest}.
     *
     * @param request {@link UiAssetCreateRequest}
     * @return {@link Asset}
     */
    public Asset fromCreateRequest(UiAssetCreateRequest request) {
        assetIdValidator.assertValid(request.getId());
        var organizationName = selfDescriptionService.getCuratorName();
        return assetMapper.buildAsset(request, organizationName);
    }

    /**
     * Returns an edited copy of an {@link Asset} updated with the given {@link UiAssetEditMetadataRequest}
     *
     * @param asset   {@link Asset} (immutable)
     * @param request {@link UiAssetEditMetadataRequest}
     * @return copy of {@link Asset}
     */
    public Asset fromEditMetadataRequest(Asset asset, UiAssetEditMetadataRequest request) {
        var createRequest = buildCreateRequest(asset, request);
        var tmpAsset = fromCreateRequest(createRequest);

        // DEBT: On each EDC update, check that no field was added in
        //  org.eclipse.edc.spi.types.domain.asset.Asset.toBuilder
        return Asset.Builder.newInstance()
                .id(asset.getId())
                .properties(tmpAsset.getProperties())
                .privateProperties(tmpAsset.getPrivateProperties())
                .dataAddress(asset.getDataAddress())
                .createdAt(asset.getCreatedAt())
                .build();
    }


    private UiAssetCreateRequest buildCreateRequest(Asset asset, UiAssetEditMetadataRequest editRequest) {
        var dataAddress = edcPropertyUtils.truncateToMapOfString(asset.getDataAddress().getProperties());

        var createRequest = new UiAssetCreateRequest();
        createRequest.setId(asset.getId());
        createRequest.setConditionsForUse(editRequest.getConditionsForUse());
        createRequest.setCustomJsonAsString(editRequest.getCustomJsonAsString());
        createRequest.setCustomJsonLdAsString(editRequest.getCustomJsonLdAsString());
        createRequest.setDataAddressProperties(dataAddress);
        createRequest.setDataCategory(editRequest.getDataCategory());
        createRequest.setDataModel(editRequest.getDataModel());
        createRequest.setDataSampleUrls(editRequest.getDataSampleUrls());
        createRequest.setDataSubcategory(editRequest.getDataSubcategory());
        createRequest.setDataUpdateFrequency(editRequest.getDataUpdateFrequency());
        createRequest.setDescription(editRequest.getDescription());
        createRequest.setGeoLocation(editRequest.getGeoLocation());
        createRequest.setGeoReferenceMethod(editRequest.getGeoReferenceMethod());
        createRequest.setKeywords(editRequest.getKeywords());
        createRequest.setLandingPageUrl(editRequest.getLandingPageUrl());
        createRequest.setLanguage(editRequest.getLanguage());
        createRequest.setLicenseUrl(editRequest.getLicenseUrl());
        createRequest.setMediaType(editRequest.getMediaType());
        createRequest.setNutsLocation(editRequest.getNutsLocation());
        createRequest.setPrivateCustomJsonAsString(editRequest.getPrivateCustomJsonAsString());
        createRequest.setPrivateCustomJsonLdAsString(editRequest.getPrivateCustomJsonLdAsString());
        createRequest.setPublisherHomepage(editRequest.getPublisherHomepage());
        createRequest.setReferenceFileUrls(editRequest.getReferenceFileUrls());
        createRequest.setReferenceFilesDescription(editRequest.getReferenceFilesDescription());
        createRequest.setSovereignLegalName(editRequest.getSovereignLegalName());
        createRequest.setTemporalCoverageFrom(editRequest.getTemporalCoverageFrom());
        createRequest.setTemporalCoverageToInclusive(editRequest.getTemporalCoverageToInclusive());
        createRequest.setTitle(editRequest.getTitle());
        createRequest.setTransportMode(editRequest.getTransportMode());
        createRequest.setVersion(editRequest.getVersion());

        return createRequest;
    }
}
