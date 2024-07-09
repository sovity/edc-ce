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

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules;

import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@RequiredArgsConstructor
public class OfflineConnectorCleanerJob implements Job {
    private final DslContextFactory dslContextFactory;
    private final OfflineConnectorCleaner offlineConnectorCleaner;

    @Override
    public void execute(JobExecutionContext context) {
        dslContextFactory.transaction(offlineConnectorCleaner::cleanConnectorsIfOfflineTooLong);
    }
}
