/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.implementation.example.services;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleItem;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ExampleService {
    private final IdsEndpointService idsEndpointService;

    public ExampleResult example(@NonNull ExampleQuery query) {
        requireNonNull(query.getName(), "name must not be null");
        Validate.notEmpty(query.getMyNestedList(), "list must not be empty");

        var exampleResult = new ExampleResult();
        exampleResult.setName(query.getName());
        exampleResult.setMyNestedItem(new ExampleItem("example"));
        exampleResult.setMyNestedList(query.getMyNestedList().stream().map(ExampleItem::new).toList());
        exampleResult.setIdsEndpoint(idsEndpointService.getIdsEndpoint());
        return exampleResult;
    }
}
