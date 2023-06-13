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

package de.sovity.edc.ext.brokerserver.dao.pages.catalog.models;

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryFields;
import org.jooq.Condition;

@FunctionalInterface
public interface CatalogQuerySelectedFilterQuery {

    /**
     * Adds a filter to a Catalog Query.
     *
     * @param fields fields and tables available in the catalog query
     * @return {@link Condition}
     */
    Condition filterDataOffers(CatalogQueryFields fields);
}
