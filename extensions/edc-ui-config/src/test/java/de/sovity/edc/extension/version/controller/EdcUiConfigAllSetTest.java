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
class EdcUiConfigAllSetTest {

    private static final String CONNECTOR_NAME = "Example Connector Name";
    private static final String CURATOR_URL = "https://example-curator.com";
    private static final String DAPS_OAUTH_JWKS_URL = "https://daps.example.com/jwks.json";
    private static final String DAPS_OAUTH_TOKEN_URL = "https://daps.example.com/token";
    private static final String IDS_DESCRIPTION = "Example Connector Description";
    private static final String IDS_ID = "urn:connector:my-id";
    private static final String IDS_TITLE = "Example Connector Title";
    private static final String MAINTAINER_URL = "https://example-maintainer.com";
    private static final String SOME_EXAMPLE_PROP = "this should also be passed through";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(
                DataPlaneInstanceStore.class,
                mock(DataPlaneInstanceStore.class));
        extension.setConfiguration(createConfiguration(Map.of(
                "edc.ids.endpoint", "http://my-edc/api/v1/ids",
                "edc.connector.name", CONNECTOR_NAME,
                "edc.ids.id", IDS_ID,
                "edc.ids.title", IDS_TITLE,
                "edc.ids.description", IDS_DESCRIPTION,
                "edc.ids.curator", CURATOR_URL,
                "edc.ids.maintainer", MAINTAINER_URL,
                "edc.oauth.token.url", DAPS_OAUTH_TOKEN_URL,
                "edc.oauth.provider.jwks.url", DAPS_OAUTH_JWKS_URL,
                "edc.ui.some.example.prop", SOME_EXAMPLE_PROP
        )));
    }

    @Test
    void testEdcUiConfigWithEverythingSet() {
        mockRequest().assertThat()
                .body("EDC_UI_CONNECTOR_ENDPOINT", equalTo("http://my-edc/api/v1/ids/data"))
                .body("EDC_UI_CONNECTOR_NAME", equalTo(CONNECTOR_NAME))
                .body("EDC_UI_IDS_ID", equalTo(IDS_ID))
                .body("EDC_UI_IDS_TITLE", equalTo(IDS_TITLE))
                .body("EDC_UI_IDS_DESCRIPTION", equalTo(IDS_DESCRIPTION))
                .body("EDC_UI_CURATOR_URL", equalTo(CURATOR_URL))
                .body("EDC_UI_MAINTAINER_URL", equalTo(MAINTAINER_URL))
                .body("EDC_UI_DAPS_OAUTH_TOKEN_URL", equalTo(DAPS_OAUTH_TOKEN_URL))
                .body("EDC_UI_DAPS_OAUTH_JWKS_URL", equalTo(DAPS_OAUTH_JWKS_URL))
                .body("EDC_UI_SOME_EXAMPLE_PROP", equalTo(SOME_EXAMPLE_PROP));
    }
}
