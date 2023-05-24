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
package de.sovity.edc.ext.brokerserver.services.queue;

public class ConnectorRefreshPriority {
    public static final int ADMIN_REQUESTED = 1;
    public static final int ADDED_ON_STARTUP = 10;
    public static final int SCHEDULED_REFRESH = 100;
}
