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

package de.sovity.edc.ext.wrapper.api.common.mappers.asset;

import de.sovity.edc.ext.wrapper.api.common.model.DataSourceType;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AssetEditRequestMapper {
    /**
     * Builds a valid {@link UiAssetCreateRequest} from a {@link UiAssetEditRequest}.
     * <p>
     * We do that to re-use the mapping for the edit process.
     *
     * @param editRequest {@link UiAssetEditRequest}
     * @return {@link UiAssetCreateRequest}
     */
    public UiAssetCreateRequest buildCreateRequest(@NonNull UiAssetEditRequest editRequest, @NonNull String assetId) {
        var dataSource = editRequest.getDataSourceOverrideOrNull();
        if (dataSource == null) {
            dataSource = dummyDataSource();
        }

        return UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(assetId)
            .title(editRequest.getTitle())
            .language(editRequest.getLanguage())
            .description(editRequest.getDescription())
            .publisherHomepage(editRequest.getPublisherHomepage())
            .licenseUrl(editRequest.getLicenseUrl())
            .version(editRequest.getVersion())
            .keywords(editRequest.getKeywords())
            .mediaType(editRequest.getMediaType())
            .landingPageUrl(editRequest.getLandingPageUrl())
            .dataCategory(editRequest.getDataCategory())
            .dataSubcategory(editRequest.getDataSubcategory())
            .dataModel(editRequest.getDataModel())
            .geoReferenceMethod(editRequest.getGeoReferenceMethod())
            .transportMode(editRequest.getTransportMode())
            .sovereignLegalName(editRequest.getSovereignLegalName())
            .geoLocation(editRequest.getGeoLocation())
            .nutsLocations(editRequest.getNutsLocations())
            .dataSampleUrls(editRequest.getDataSampleUrls())
            .referenceFileUrls(editRequest.getReferenceFileUrls())
            .referenceFilesDescription(editRequest.getReferenceFilesDescription())
            .conditionsForUse(editRequest.getConditionsForUse())
            .dataUpdateFrequency(editRequest.getDataUpdateFrequency())
            .temporalCoverageFrom(editRequest.getTemporalCoverageFrom())
            .temporalCoverageToInclusive(editRequest.getTemporalCoverageToInclusive())
            .customJsonAsString(editRequest.getCustomJsonAsString())
            .customJsonLdAsString(editRequest.getCustomJsonLdAsString())
            .privateCustomJsonAsString(editRequest.getPrivateCustomJsonAsString())
            .privateCustomJsonLdAsString(editRequest.getPrivateCustomJsonLdAsString())
            .build();
    }

    private UiDataSource dummyDataSource() {
        return UiDataSource.builder().type(DataSourceType.CUSTOM).build();
    }
}
