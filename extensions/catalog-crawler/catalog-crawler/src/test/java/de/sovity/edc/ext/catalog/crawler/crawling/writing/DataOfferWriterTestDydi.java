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

import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import lombok.Value;
import org.eclipse.edc.spi.system.configuration.Config;

import static org.mockito.Mockito.mock;

@Value
class DataOfferWriterTestDydi {
    Config config = mock(Config.class);
    CrawlerConfig crawlerConfig = mock(CrawlerConfig.class);
    DataOfferQueries dataOfferQueries = new DataOfferQueries();
    ContractOfferQueries contractOfferQueries = new ContractOfferQueries();
    ContractOfferRecordUpdater contractOfferRecordUpdater = new ContractOfferRecordUpdater();
    ConnectorQueries connectorQueries = new ConnectorQueries(crawlerConfig);
    ShortDescriptionBuilder shortDescriptionBuilder = new ShortDescriptionBuilder();
    DataOfferRecordUpdater dataOfferRecordUpdater = new DataOfferRecordUpdater(shortDescriptionBuilder);
    CatalogPatchBuilder catalogPatchBuilder = new CatalogPatchBuilder(
            contractOfferQueries,
            dataOfferQueries,
            dataOfferRecordUpdater,
            contractOfferRecordUpdater
    );
    CatalogPatchApplier catalogPatchApplier = new CatalogPatchApplier();
    ConnectorUpdateCatalogWriter connectorUpdateCatalogWriter = new ConnectorUpdateCatalogWriter(catalogPatchBuilder, catalogPatchApplier);

    // for the ConnectorUpdateSuccessWriterTest
    CrawlerEventLogger crawlerEventLogger = new CrawlerEventLogger();
    DataOfferLimitsEnforcer dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(
            crawlerConfig,
            crawlerEventLogger
    );
    ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(
            crawlerEventLogger,
            connectorUpdateCatalogWriter,
            dataOfferLimitsEnforcer
    );
}
