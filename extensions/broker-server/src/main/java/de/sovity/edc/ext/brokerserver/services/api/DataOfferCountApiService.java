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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.api.model.DataOfferCountResult;
import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class DataOfferCountApiService {

    public DataOfferCountResult countByEndpoints(DSLContext dsl, List<String> endpoints) {
        var d = Tables.DATA_OFFER;

        var count = DSL.count().as("count");
        var numDataOffers = dsl.select(d.CONNECTOR_ENDPOINT, count)
            .from(d)
            .where(PostgresqlUtils.in(d.CONNECTOR_ENDPOINT, endpoints))
            .groupBy(d.CONNECTOR_ENDPOINT)
            .fetchMap(d.CONNECTOR_ENDPOINT, count);

        var numDataOffersFilled = endpoints.stream().distinct().collect(toMap(
            Function.identity(),
            endpoint -> numDataOffers.getOrDefault(endpoint, 0)
        ));

        return new DataOfferCountResult(numDataOffersFilled);
    }
}
