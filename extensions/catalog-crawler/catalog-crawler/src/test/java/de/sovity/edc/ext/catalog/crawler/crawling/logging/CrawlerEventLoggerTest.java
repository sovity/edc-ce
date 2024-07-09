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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class CrawlerEventLoggerTest {
    @RegisterExtension
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var crawlerEventLogger = new CrawlerEventLogger();

            // Test that insertions insert required fields and don't cause DB errors
            var connectorRef = new ConnectorRef(
                    "MDSL1234XX.C1234XX",
                    "test",
                    "My Org",
                    "MDSL1234XX",
                    "https://example.com/api/dsp"
            );
            crawlerEventLogger.logConnectorUpdated(dsl, connectorRef, new ConnectorChangeTracker());
            crawlerEventLogger.logConnectorOnline(dsl, connectorRef);
            crawlerEventLogger.logConnectorOffline(dsl, connectorRef, new CrawlerEventErrorMessage("Message", "Stacktrace"));
            crawlerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, connectorRef, 10);
            crawlerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, connectorRef);
            crawlerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, connectorRef, 10);
            crawlerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, connectorRef);

            assertThat(numLogEntries(dsl)).isEqualTo(7);
        });
    }

    private Integer numLogEntries(DSLContext dsl) {
        return dsl.selectCount().from(Tables.CRAWLER_EVENT_LOG).fetchOne().component1();
    }
}
