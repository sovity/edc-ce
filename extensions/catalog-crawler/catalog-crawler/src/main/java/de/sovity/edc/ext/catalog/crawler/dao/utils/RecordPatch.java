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

package de.sovity.edc.ext.catalog.crawler.dao.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jooq.UpdatableRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains planned DB Row changes to be applied as batch.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordPatch<T extends UpdatableRecord<T>> {
    List<T> insertions = new ArrayList<>();
    List<T> updates = new ArrayList<>();
    List<T> deletions = new ArrayList<>();

    public void insert(T record) {
        insertions.add(record);
    }

    public void update(T record) {
        updates.add(record);
    }

    public void delete(T record) {
        deletions.add(record);
    }
}
