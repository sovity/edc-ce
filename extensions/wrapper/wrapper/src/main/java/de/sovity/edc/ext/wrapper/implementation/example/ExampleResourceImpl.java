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

package de.sovity.edc.ext.wrapper.implementation.example;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExampleResourceImpl implements ExampleResource {

    private final ExampleApiService exampleApiService;

    @Override
    public ExampleResult exampleEndpoint(ExampleQuery query) {
        return exampleApiService.exampleEndpoint(query);
    }
}
