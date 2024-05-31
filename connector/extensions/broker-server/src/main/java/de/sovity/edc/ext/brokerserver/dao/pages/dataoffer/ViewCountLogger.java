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

package de.sovity.edc.ext.brokerserver.dao.pages.dataoffer;

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

public class ViewCountLogger {
    public void increaseDataOfferViewCount(DSLContext dsl, String assetId, String endpoint) {
        var v = Tables.DATA_OFFER_VIEW_COUNT;
        dsl.insertInto(v, v.ASSET_ID, v.CONNECTOR_ENDPOINT, v.DATE).values(assetId, endpoint, OffsetDateTime.now()).execute();
    }
}
