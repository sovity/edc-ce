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

package de.sovity.edc.ext.catalog.crawler.orchestration.config;

import de.sovity.edc.ext.catalog.crawler.CrawlerExtension;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;

@RequiredArgsConstructor
public class CrawlerConfigFactory {
    private final Config config;

    public CrawlerConfig buildCrawlerConfig() {
        var environmentId = config.getString(CrawlerExtension.ENVIRONMENT_ID);
        var numThreads = config.getInteger(CrawlerExtension.NUM_THREADS, 1);
        var killOfflineConnectorsAfter = getDuration(CrawlerExtension.KILL_OFFLINE_CONNECTORS_AFTER, Duration.ofDays(5));
        var maxDataOffers = config.getInteger(CrawlerExtension.MAX_DATA_OFFERS_PER_CONNECTOR, -1);
        var maxContractOffers = config.getInteger(CrawlerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER, -1);

        return CrawlerConfig.builder()
                .environmentId(environmentId)
                .numThreads(numThreads)
                .killOfflineConnectorsAfter(killOfflineConnectorsAfter)
                .maxDataOffersPerConnector(maxDataOffers)
                .maxContractOffersPerDataOffer(maxContractOffers)
                .build();
    }

    private Duration getDuration(@NonNull String configProperty, Duration defaultValue) {
        var value = config.getString(configProperty, "");

        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        return Duration.parse(value);
    }
}
