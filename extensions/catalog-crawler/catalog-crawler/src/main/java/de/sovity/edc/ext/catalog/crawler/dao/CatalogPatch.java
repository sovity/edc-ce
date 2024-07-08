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

package de.sovity.edc.ext.catalog.crawler.dao;

import de.sovity.edc.ext.catalog.crawler.dao.utils.RecordPatch;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.DataOfferRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Contains planned DB Row changes to be applied as batch.
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogPatch {
    RecordPatch<DataOfferRecord> dataOffers = new RecordPatch<>();
    RecordPatch<ContractOfferRecord> contractOffers = new RecordPatch<>();

    public List<RecordPatch<?>> insertionOrder() {
        return List.of(dataOffers, contractOffers);
    }
}
