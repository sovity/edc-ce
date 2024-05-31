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
import de.sovity.edc.ext.brokerserver.dao.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;

import java.util.List;

@RequiredArgsConstructor
public class CatalogSearchService {

    public Condition filterBySearch(CatalogQueryFields fields, String searchQuery) {
        return SearchUtils.simpleSearch(searchQuery, List.of(
            fields.getDataOfferTable().ASSET_ID,
            fields.getDataOfferTable().ASSET_TITLE,
            fields.getDataOfferTable().DATA_CATEGORY,
            fields.getDataOfferTable().DATA_SUBCATEGORY,
            fields.getDataOfferTable().DESCRIPTION,
            fields.getDataOfferTable().CURATOR_ORGANIZATION_NAME,
            fields.getDataOfferTable().KEYWORDS_COMMA_JOINED,
            fields.getConnectorTable().ENDPOINT,
            fields.getConnectorTable().PARTICIPANT_ID,
            fields.getOrganizationName()
        ));
    }
}
