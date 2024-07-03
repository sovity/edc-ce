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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.services.ConnectorCreator;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class DataOfferWriterTestDataHelper {
    ConnectorRef connectorRef = new ConnectorRef(
            "MDSL1234XX.C1234XX",
            "test",
            "My Org",
            "MDSL1234XX",
            "https://example.com/api/dsp"
    );
    ;
    OffsetDateTime old = OffsetDateTime.now().withNano(0).withSecond(0).withMinute(0).withHour(0).minusDays(100);
    List<ContractOfferRecord> existingContractOffers = new ArrayList<>();
    List<DataOfferRecord> existingDataOffers = new ArrayList<>();
    List<FetchedDataOffer> fetchedDataOffers = new ArrayList<>();


    /**
     * Adds fetched data offer
     *
     * @param dataOffer fetched data offer
     */
    public void fetched(DataOfferWriterTestDataModels.Do dataOffer) {
        Validate.notEmpty(dataOffer.getContractOffers());
        fetchedDataOffers.add(dummyFetchedDataOffer(dataOffer));
    }


    /**
     * Adds data offer directly to DB.
     *
     * @param dataOffer data offer
     */
    public void existing(DataOfferWriterTestDataModels.Do dataOffer) {
        Validate.notEmpty(dataOffer.getContractOffers());
        existingDataOffers.add(dummyDataOffer(dataOffer));
        dataOffer.getContractOffers().stream()
                .map(contractOffer -> dummyContractOffer(dataOffer, contractOffer))
                .forEach(existingContractOffers::add);
    }

    public void initialize(DSLContext dsl) {
        var connectorQueries = new ConnectorQueries();
        var connectorCreator = new ConnectorCreator(connectorQueries);
        connectorCreator.addConnector(dsl, connectorRef);
        dsl.batchInsert(existingDataOffers).execute();
        dsl.batchInsert(existingContractOffers).execute();
    }

    private ContractOfferRecord dummyContractOffer(DataOfferWriterTestDataModels.Do dataOffer,
                                                   DataOfferWriterTestDataModels.Co contractOffer) {
        var contractOfferRecord = new ContractOfferRecord();
        contractOfferRecord.setConnectorEndpoint(connectorRef);
        contractOfferRecord.setAssetId(dataOffer.getAssetId());
        contractOfferRecord.setContractOfferId(contractOffer.getId());
        contractOfferRecord.setPolicy(JSONB.valueOf(dummyPolicyJson(contractOffer.getPolicyValue())));
        contractOfferRecord.setCreatedAt(old);
        contractOfferRecord.setUpdatedAt(old);
        return contractOfferRecord;
    }

    private DataOfferRecord dummyDataOffer(DataOfferWriterTestDataModels.Do dataOffer) {
        var assetName = Optional.of(dataOffer.getAssetTitle()).orElse(dataOffer.getAssetId());

        var dataOfferRecord = new DataOfferRecord();
        dataOfferRecord.setConnectorEndpoint(connectorRef);
        dataOfferRecord.setAssetId(dataOffer.getAssetId());
        dataOfferRecord.setAssetTitle(assetName);
        dataOfferRecord.setAssetJsonLd(JSONB.valueOf(dummyAssetJson(dataOffer)));
        dataOfferRecord.setCreatedAt(old);
        dataOfferRecord.setUpdatedAt(old);
        return dataOfferRecord;
    }

    private FetchedDataOffer dummyFetchedDataOffer(DataOfferWriterTestDataModels.Do dataOffer) {
        var fetchedDataOffer = new FetchedDataOffer();
        fetchedDataOffer.setAssetId(dataOffer.getAssetId());
        fetchedDataOffer.setAssetTitle(dataOffer.getAssetTitle());
        fetchedDataOffer.setAssetJsonLd(dummyAssetJson(dataOffer));

        var contractOffersMapped = dataOffer.getContractOffers().stream().map(this::dummyFetchedContractOffer).collect(Collectors.toList());
        fetchedDataOffer.setContractOffers(contractOffersMapped);

        return fetchedDataOffer;
    }

    public String dummyAssetJson(DataOfferWriterTestDataModels.Do dataOffer) {
        var dummyUiAssetJson = Json.createObjectBuilder()
                .add("assetId", dataOffer.getAssetId())
                .add("title", dataOffer.getAssetTitle())
                .add("assetJsonLd", "{}")
                .build();
        return JsonUtils.toJson(dummyUiAssetJson);
    }

    public String dummyPolicyJson(String policyValue) {
        return "{\"%s\": \"%s\"}".formatted(
                "SomePolicyField", policyValue
        );
    }

    @NotNull
    private FetchedContractOffer dummyFetchedContractOffer(DataOfferWriterTestDataModels.Co it) {
        var contractOffer = new FetchedContractOffer();
        contractOffer.setContractOfferId(it.getId());
        contractOffer.setPolicyJson(dummyPolicyJson(it.getPolicyValue()));
        return contractOffer;
    }
}
