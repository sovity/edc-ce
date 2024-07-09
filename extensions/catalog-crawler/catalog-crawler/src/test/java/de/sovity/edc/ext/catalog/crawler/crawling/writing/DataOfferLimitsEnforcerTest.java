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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataOfferLimitsEnforcerTest {
    DataOfferLimitsEnforcer dataOfferLimitsEnforcer;
    CrawlerConfig settings;
    CrawlerEventLogger crawlerEventLogger;
    DSLContext dsl;

    ConnectorRef connectorRef = new DataOfferWriterTestDataHelper().connectorRef;

    @BeforeEach
    void setup() {
        settings = mock(CrawlerConfig.class);
        crawlerEventLogger = mock(CrawlerEventLogger.class);
        dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(settings, crawlerEventLogger);
        dsl = mock(DSLContext.class);
    }

    @Test
    void no_limit_and_two_dataofffers_and_contractoffer_should_not_limit() {
        // arrange
        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = enforcedLimits.abbreviatedDataOffers();
        var contractOffersLimitExceeded = enforcedLimits.contractOfferLimitsExceeded();
        var dataOffersLimitExceeded = enforcedLimits.dataOfferLimitsExceeded();

        // assert
        Assertions.assertThat(actual).hasSize(2);
        assertFalse(contractOffersLimitExceeded);
        assertFalse(dataOffersLimitExceeded);
    }

    @Test
    void limit_zero_and_one_dataoffers_should_result_to_none() {
        // arrange
        int maxDataOffers = 0;
        int maxContractOffers = 0;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var dataOffers = List.of(new FetchedDataOffer());

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = new ArrayList<>(enforcedLimits.abbreviatedDataOffers());
        var contractOffersLimitExceeded = enforcedLimits.contractOfferLimitsExceeded();
        var dataOffersLimitExceeded = enforcedLimits.dataOfferLimitsExceeded();

        // assert
        assertThat(actual).isEmpty();
        assertFalse(contractOffersLimitExceeded);
        assertTrue(dataOffersLimitExceeded);
    }

    @Test
    void limit_one_and_two_dataoffers_should_result_to_one() {
        // arrange
        int maxDataOffers = 1;
        int maxContractOffers = 1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = new ArrayList<>(enforcedLimits.abbreviatedDataOffers());
        var contractOffersLimitExceeded = enforcedLimits.contractOfferLimitsExceeded();
        var dataOffersLimitExceeded = enforcedLimits.dataOfferLimitsExceeded();

        // assert
        assertThat(actual).hasSize(1);
        Assertions.assertThat(actual.get(0).getContractOffers()).hasSize(1);
        assertTrue(contractOffersLimitExceeded);
        assertTrue(dataOffersLimitExceeded);
    }

    @Test
    void verify_logConnectorUpdateDataOfferLimitExceeded() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);

        int maxDataOffers = 1;
        int maxContractOffers = 1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, enforcedLimits);

        // assert
        verify(crawlerEventLogger).logConnectorUpdateDataOfferLimitExceeded(dsl, connectorRef, 1);
    }

    @Test
    void verify_logConnectorUpdateDataOfferLimitOk() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);

        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, enforcedLimits);

        // assert
        verify(crawlerEventLogger).logConnectorUpdateDataOfferLimitOk(dsl, connectorRef);
    }

    @Test
    void verify_logConnectorUpdateContractOfferLimitExceeded() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);

        int maxDataOffers = 1;
        int maxContractOffers = 1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, enforcedLimits);

        // assert
        verify(crawlerEventLogger).logConnectorUpdateContractOfferLimitExceeded(dsl, connectorRef, 1);
    }

    @Test
    void verify_logConnectorUpdateContractOfferLimitOk() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);

        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(settings.getMaxDataOffersPerConnector()).thenReturn(maxDataOffers);
        when(settings.getMaxContractOffersPerDataOffer()).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedContractOffer(), new FetchedContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, enforcedLimits);

        // assert
        verify(crawlerEventLogger).logConnectorUpdateContractOfferLimitOk(dsl, connectorRef);
    }
}
