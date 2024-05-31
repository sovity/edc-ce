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

package de.sovity.edc.ext.brokerserver.services.api.filtering;

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryFields;
import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import org.jooq.Field;

import java.util.function.Function;

public class CatalogFilterAttributeDefinitionService {

    public CatalogFilterAttributeDefinition forField(
            Function<CatalogQueryFields, Field<String>> fieldExtractor,
            String name,
            String label
    ) {
        return new CatalogFilterAttributeDefinition(
            name,
            label,
            fieldExtractor::apply,
            (fields, values) -> PostgresqlUtils.in(fieldExtractor.apply(fields), values)
        );
    }

    public CatalogFilterAttributeDefinition buildDataSpaceFilter() {
        return new CatalogFilterAttributeDefinition(
            "dataSpace",
            "Data Space",
            CatalogQueryFields::getDataSpace,
            (fields, values) -> PostgresqlUtils.in(fields.getDataSpace(), values)
        );
    }

    public CatalogFilterAttributeDefinition buildConnectorEndpointFilter() {
        return new CatalogFilterAttributeDefinition(
            "connectorEndpoint",
            "Connector",
            fields -> fields.getDataOfferTable().CONNECTOR_ENDPOINT,
            (fields, values) -> PostgresqlUtils.in(fields.getDataOfferTable().CONNECTOR_ENDPOINT, values)
        );
    }
}
