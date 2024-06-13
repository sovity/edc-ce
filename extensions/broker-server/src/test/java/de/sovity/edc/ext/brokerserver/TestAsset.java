/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetEditRequestMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpHeaderMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceType;
import jakarta.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestAsset {

    public static JsonObject getAssetJsonLd(String assetId) {
        return getAssetJsonLd(
            UiAssetCreateRequest.builder()
                .id(assetId)
                .build()
        );
    }

    public static JsonObject getAssetJsonLd(UiAssetCreateRequest request) {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .build())
            .build();
        var withDataSource = request.toBuilder().dataSource(dataSource).build();
        return buildAssetJsonLdBuilder().createAssetJsonLd(withDataSource, "orgName");
    }

    /**
     * Sets assetJsonLd and other extracted fields.
     * <p>
     * This method keeps our tests consistent if we change the extracted fields.
     *
     * @param dataOfferRecord data offer record to be updated
     * @param assetJsonLd     asset json ld
     * @param participantId   required because the organization name will default to the participant id if unset
     */
    public static void setDataOfferAssetMetadata(DataOfferRecord dataOfferRecord, JsonObject assetJsonLd, String participantId) {
        // We trickily use the real code to update all the extracted values from the asset JSON-LD
        var fetchedCatalogBuilder = BrokerServerExtensionContext.instance.fetchedCatalogBuilder();
        var dataOfferRecordUpdater = BrokerServerExtensionContext.instance.dataOfferRecordUpdater();

        var fetchedDataOffer = new FetchedDataOffer();
        fetchedCatalogBuilder.setAssetMetadata(fetchedDataOffer, assetJsonLd, participantId);

        dataOfferRecord.setAssetId(fetchedDataOffer.getAssetId());
        dataOfferRecordUpdater.updateDataOffer(dataOfferRecord, fetchedDataOffer, false);
    }

    public static AssetJsonLdBuilder buildAssetJsonLdBuilder() {
        return new AssetJsonLdBuilder(
            new DataSourceMapper(
                new EdcPropertyUtils(),
                new HttpDataSourceMapper(new HttpHeaderMapper())
            ),
            buildAssetJsonLdParser(),
            new AssetEditRequestMapper()
        );
    }

    @NotNull
    private static AssetJsonLdParser buildAssetJsonLdParser() {
        return new AssetJsonLdParser(
            new AssetJsonLdUtils(),
            new ShortDescriptionBuilder(),
            "https://my-connector"::equals
        );
    }
}
