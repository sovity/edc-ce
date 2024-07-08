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

package de.sovity.edc.ext.catalog.crawler;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogBuilder;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;


/**
 * Manual Dependency Injection result
 *
 * @param crawlerInitializer Startup Logic
 */
public record CrawlerExtensionContext(
        CrawlerInitializer crawlerInitializer,
        // Required for stopping connections on closing
        HikariDataSource dataSource,
        DslContextFactory dslContextFactory,

        // Required for Integration Tests
        ConnectorCrawler connectorCrawler,
        PolicyMapper policyMapper,
        FetchedCatalogBuilder catalogPatchBuilder,
        DataOfferRecordUpdater dataOfferRecordUpdater
) {
}
