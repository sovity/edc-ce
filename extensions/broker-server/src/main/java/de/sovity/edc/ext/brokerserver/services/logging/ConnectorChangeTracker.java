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

package de.sovity.edc.ext.brokerserver.services.logging;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for collecting the information required to build log messages about what was updated.
 */
@Getter
public class ConnectorChangeTracker {
    @Setter
    private int numOffersAdded = 0;

    @Setter
    private int numOffersDeleted = 0;

    @Setter
    private int numOffersUpdated = 0;

    public boolean isEmpty() {
        return numOffersAdded == 0 && numOffersDeleted == 0 && numOffersUpdated == 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Connector is up to date.";
        }

        var msg = "Connector Updated.";
        if (numOffersAdded > 0 || numOffersDeleted > 0 || numOffersUpdated > 0) {
            List<String> offersMsgs = new ArrayList<>();
            if (numOffersAdded > 0) {
                offersMsgs.add("%d added".formatted(numOffersAdded));
            }
            if (numOffersUpdated > 0) {
                offersMsgs.add("%d updated".formatted(numOffersUpdated));
            }
            if (numOffersDeleted > 0) {
                offersMsgs.add("%d deleted".formatted(numOffersDeleted));
            }
            msg += " Data Offers changed: %s.".formatted(String.join(", ", offersMsgs));
        }
        return msg;
    }
}
