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

import de.sovity.edc.ext.brokerserver.dao.stores.ContractOfferStore;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CatalogApiService {
    private final ContractOfferStore contractOfferStore;
    private final PaginationMetadataUtils paginationMetadataUtils;

    public CatalogPageResult catalogPage(CatalogPageQuery query) {
        throw new IllegalStateException("Not implemented yet");
    }
}
