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
import de.sovity.edc.ext.catalog.crawler.utils.CollectionUtils2;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class CatalogPatchApplier {

    @SneakyThrows
    public void applyDbUpdatesBatched(DSLContext dsl, CatalogPatch catalogPatch) {
        var insertionOrder = catalogPatch.insertionOrder();
        var deletionOrder = CollectionUtils2.reverse(insertionOrder);

        insertionOrder.stream()
                .map(RecordPatch::getInsertions)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchInsert(it).execute());

        insertionOrder.stream()
                .map(RecordPatch::getUpdates)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchUpdate(it).execute());

        deletionOrder.stream()
                .map(RecordPatch::getDeletions)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchDelete(it).execute());
    }
}
