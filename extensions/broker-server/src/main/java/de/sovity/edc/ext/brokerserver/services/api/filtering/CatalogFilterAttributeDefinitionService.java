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
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class CatalogFilterAttributeDefinitionService {

    public CatalogFilterAttributeDefinition fromAssetProperty(String assetProperty, String label) {
        return new CatalogFilterAttributeDefinition(
                assetProperty,
                label,
                fields -> getValue(fields, assetProperty),
                (fields, values) -> PostgresqlUtils.in(getValue(fields, assetProperty), values)
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

    @NotNull
    private Field<String> getValue(CatalogQueryFields fields, String assetProperty) {
        return DSL.coalesce(fields.getAssetProperty(assetProperty), DSL.value(""));
    }
}
