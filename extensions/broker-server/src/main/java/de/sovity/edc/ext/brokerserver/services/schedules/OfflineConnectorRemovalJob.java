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

package de.sovity.edc.ext.brokerserver.services.schedules;

import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.services.OfflineConnectorRemover;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@RequiredArgsConstructor
public class OfflineConnectorRemovalJob implements Job {
    private final DslContextFactory dslContextFactory;
    private final OfflineConnectorRemover offlineConnectorRemover;

    @Override
    public void execute(JobExecutionContext context) {
        dslContextFactory.transaction(offlineConnectorRemover::removeIfOfflineTooLong);
    }
}
