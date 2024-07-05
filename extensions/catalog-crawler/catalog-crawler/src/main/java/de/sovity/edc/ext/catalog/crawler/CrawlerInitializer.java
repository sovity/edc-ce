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

import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.QuartzScheduleInitializer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrawlerInitializer {
    private final QuartzScheduleInitializer quartzScheduleInitializer;

    public void onStartup() {
        quartzScheduleInitializer.startSchedules();
    }
}
