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

        return asset.toBuilder()
                .properties(tmpAsset.getProperties())
                .privateProperties(tmpAsset.getPrivateProperties())
                .build();
    }


    private UiAssetCreateRequest buildCreateRequest(Asset asset, UiAssetEditMetadataRequest editRequest) {
        var dataAddress = edcPropertyUtils.truncateToMapOfString(asset.getDataAddress().getProperties());

        var createRequest = new UiAssetCreateRequest();
        createRequest.setId(asset.getId());
        createRequest.setDataAddressProperties(dataAddress);
        createRequest.setAdditionalJsonProperties(editRequest.getAdditionalJsonProperties());
        createRequest.setAdditionalProperties(editRequest.getAdditionalProperties());
        createRequest.setDataCategory(editRequest.getDataCategory());
        createRequest.setDataModel(editRequest.getDataModel());
        createRequest.setDataSubcategory(editRequest.getDataSubcategory());
        createRequest.setDescription(editRequest.getDescription());
        createRequest.setGeoReferenceMethod(editRequest.getGeoReferenceMethod());
        createRequest.setKeywords(editRequest.getKeywords());
        createRequest.setLandingPageUrl(editRequest.getLandingPageUrl());
        createRequest.setLanguage(editRequest.getLanguage());
        createRequest.setLicenseUrl(editRequest.getLicenseUrl());
        createRequest.setMediaType(editRequest.getMediaType());
        createRequest.setPrivateJsonProperties(editRequest.getPrivateJsonProperties());
        createRequest.setPrivateProperties(editRequest.getPrivateProperties());
        createRequest.setPublisherHomepage(editRequest.getPublisherHomepage());
        createRequest.setTitle(editRequest.getTitle());
        createRequest.setTransportMode(editRequest.getTransportMode());
        createRequest.setVersion(editRequest.getVersion());
        createRequest.setSovereignLegalName(editRequest.getSovereignLegalName());
        createRequest.setGeoLocation(editRequest.getGeoLocation());
        createRequest.setNutsLocation(editRequest.getNutsLocation());
        createRequest.setDataSampleUrls(editRequest.getDataSampleUrls());
        createRequest.setReferenceFileUrls(editRequest.getReferenceFileUrls());
        createRequest.setReferenceFilesDescription(editRequest.getReferenceFilesDescription());
        createRequest.setConditionsForUse(editRequest.getConditionsForUse());
        createRequest.setDataUpdateFrequency(editRequest.getDataUpdateFrequency());
        createRequest.setTemporalCoverageFrom(editRequest.getTemporalCoverageFrom());
        createRequest.setTemporalCoverageToInclusive(editRequest.getTemporalCoverageToInclusive());

        return createRequest;
    }
}
