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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOfferContractOffer;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataOfferLimitsEnforcerTest {
    DataOfferLimitsEnforcer dataOfferLimitsEnforcer;
    Config config;
    BrokerEventLogger brokerEventLogger;

    @BeforeEach
    void setup() {
        config = mock(Config.class);
        brokerEventLogger = mock(BrokerEventLogger.class);
        dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(config, brokerEventLogger);
    }

    @Test
    void no_limit_and_two_dataofffers_and_contractoffer_should_not_limit() {
        // arrange
        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = enforcedLimits.abbreviatedDataOffers();
        var contractOffersLimitExceeded = enforcedLimits.contractOfferLimitsExceeded();
        var dataOffersLimitExceeded = enforcedLimits.dataOfferLimitsExceeded();

        // assert
        assertThat(actual).hasSize(2);
        assertFalse(contractOffersLimitExceeded);
        assertFalse(dataOffersLimitExceeded);
    }

    @Test
    void limit_zero_and_one_dataoffers_should_result_to_none() {
        // arrange
        int maxDataOffers = 0;
        int maxContractOffers = 0;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var dataOffers = List.of(new FetchedDataOffer());

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = new ArrayList(enforcedLimits.abbreviatedDataOffers());
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
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        var actual = new ArrayList(enforcedLimits.abbreviatedDataOffers());
        var contractOffersLimitExceeded = enforcedLimits.contractOfferLimitsExceeded();
        var dataOffersLimitExceeded = enforcedLimits.dataOfferLimitsExceeded();

        // assert
        assertThat(actual).hasSize(1);
        assertThat(((FetchedDataOffer) actual.get(0)).getContractOffers()).hasSize(1);
        assertTrue(contractOffersLimitExceeded);
        assertTrue(dataOffersLimitExceeded);
    }

    @Test
    void verify_logConnectorUpdateDataOfferLimitExceeded() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setEndpoint("http://localhost:8080");
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);

        int maxDataOffers = 1;
        int maxContractOffers = 1;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(connector, enforcedLimits);

        // assert
        verify(brokerEventLogger).logConnectorUpdateDataOfferLimitExceeded(1, connector.getEndpoint());
    }

    @Test
    void verify_logConnectorUpdateDataOfferLimitOk() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setEndpoint("http://localhost:8080");
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);

        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(connector, enforcedLimits);

        // assert
        verify(brokerEventLogger).logConnectorUpdateDataOfferLimitOk(connector.getEndpoint());
    }

    @Test
    void verify_logConnectorUpdateContractOfferLimitExceeded() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setEndpoint("http://localhost:8080");
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);

        int maxDataOffers = 1;
        int maxContractOffers = 1;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(connector, enforcedLimits);

        // assert
        verify(brokerEventLogger).logConnectorUpdateContractOfferLimitExceeded(1, connector.getEndpoint());
    }

    @Test
    void verify_logConnectorUpdateContractOfferLimitOk() {
        // arrange
        var connector = new ConnectorRecord();
        connector.setEndpoint("http://localhost:8080");
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);

        int maxDataOffers = -1;
        int maxContractOffers = -1;
        when(config.getInteger(eq(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR), any())).thenReturn(maxDataOffers);
        when(config.getInteger(eq(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER), any())).thenReturn(maxContractOffers);

        var myDataOffer = new FetchedDataOffer();
        myDataOffer.setContractOffers(List.of(new FetchedDataOfferContractOffer(), new FetchedDataOfferContractOffer()));
        var dataOffers = List.of(myDataOffer, myDataOffer);

        // act
        var enforcedLimits = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(connector, enforcedLimits);

        // assert
        verify(brokerEventLogger).logConnectorUpdateContractOfferLimitOk(connector.getEndpoint());
    }
}
