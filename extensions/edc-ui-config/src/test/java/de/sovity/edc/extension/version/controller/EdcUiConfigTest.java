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

package de.sovity.edc.extension.version.controller;

import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static de.sovity.edc.extension.version.controller.TestUtils.createConfiguration;
import static de.sovity.edc.extension.version.controller.TestUtils.mockRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
class EdcUiConfigTest {
    private static final String SOME_EXAMPLE_PROP = "this should also be passed through";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(
                DataPlaneInstanceStore.class,
                mock(DataPlaneInstanceStore.class));
        extension.setConfiguration(createConfiguration(Map.of(
                "edc.ui.some.example.prop", SOME_EXAMPLE_PROP
        )));
    }

    @Test
    void testEdcUiConfigWithEverythingSet() {
        mockRequest().assertThat()
                .body("EDC_UI_SOME_EXAMPLE_PROP", equalTo(SOME_EXAMPLE_PROP));
    }
}
