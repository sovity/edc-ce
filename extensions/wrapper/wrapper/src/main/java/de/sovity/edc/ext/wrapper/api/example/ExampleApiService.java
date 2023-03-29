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

package de.sovity.edc.ext.wrapper.api.example;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleItem;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import de.sovity.edc.ext.wrapper.api.example.services.IdsEndpointService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ExampleApiService {
    private final IdsEndpointService idsEndpointService;

    public ExampleResult exampleEndpoint(@NonNull ExampleQuery query) {
        requireNonNull(query.getName(), "name must not be null");
        Validate.notEmpty(query.getMyNestedList(), "list must not be empty");

        var testResult = new ExampleResult();
        testResult.setName(query.getName());
        testResult.setMyNestedItem(new ExampleItem("test"));
        testResult.setMyNestedList(query.getMyNestedList().stream().map(ExampleItem::new).toList());
        testResult.setIdsEndpoint(idsEndpointService.getIdsEndpoint());
        return testResult;
    }
}
