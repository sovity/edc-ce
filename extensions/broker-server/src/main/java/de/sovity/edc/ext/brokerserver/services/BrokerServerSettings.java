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

package de.sovity.edc.ext.brokerserver.services;

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;

public class BrokerServerSettings {
    private final Config config;

    @Getter
    private final Duration hideOfflineDataOffersAfter;

    @Getter
    private final int catalogPagePageSize;

    public BrokerServerSettings(Config config) {
        this.config = config;
        hideOfflineDataOffersAfter = getDurationOrNull(BrokerServerExtension.HIDE_OFFLINE_DATA_OFFERS_AFTER);
        catalogPagePageSize = config.getInteger(BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, 20);
    }

    private Duration getDurationOrNull(@NonNull String configProperty) {
        var durationAsString = config.getString(configProperty, "");
        if (StringUtils.isBlank(durationAsString)) {
            return null;
        }

        return Duration.parse(durationAsString);
    }
}
