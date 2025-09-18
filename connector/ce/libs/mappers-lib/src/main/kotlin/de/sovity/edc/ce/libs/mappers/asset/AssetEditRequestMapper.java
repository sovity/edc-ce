/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.asset;

import de.sovity.edc.ce.api.common.model.DataSourceType;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ce.api.common.model.UiDataSource;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
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
            .dataModel(editRequest.getDataModel())
            .sovereignLegalName(editRequest.getSovereignLegalName())
            .dataSampleUrls(editRequest.getDataSampleUrls())
            .referenceFileUrls(editRequest.getReferenceFileUrls())
            .referenceFilesDescription(editRequest.getReferenceFilesDescription())
            .conditionsForUse(editRequest.getConditionsForUse())
            .dataUpdateFrequency(editRequest.getDataUpdateFrequency())
            .temporalCoverageFrom(editRequest.getTemporalCoverageFrom())
            .temporalCoverageToInclusive(editRequest.getTemporalCoverageToInclusive())
            .sphinxFields(editRequest.getSphinxFields())
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
