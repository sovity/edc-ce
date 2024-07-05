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

package de.sovity.edc.ext.catalog.crawler.orchestration.queue;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConnectorRefreshPriority {
    public static final int SCHEDULED_ONLINE_REFRESH = 100;
    public static final int SCHEDULED_OFFLINE_REFRESH = 200;
    public static final int SCHEDULED_DEAD_REFRESH = 300;
}
