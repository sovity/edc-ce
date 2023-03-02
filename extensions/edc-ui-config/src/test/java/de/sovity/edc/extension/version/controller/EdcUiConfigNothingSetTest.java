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

import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static de.sovity.edc.extension.version.controller.TestUtils.createConfiguration;
import static de.sovity.edc.extension.version.controller.TestUtils.mockRequest;
import static org.hamcrest.Matchers.equalTo;

@ApiTest
@ExtendWith(EdcExtension.class)
class EdcUiConfigNothingSetTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(Map.of()));
    }

    @Test
    void testEdcUiConfigWithNothingSet() {
        mockRequest().assertThat()
                .body("EDC_UI_CONNECTOR_ENDPOINT", equalTo("http://property.edc-ids-endpoint.not.set.in.edc.backend/ids/data"))
                .body("EDC_UI_CONNECTOR_NAME", equalTo(missingMessage("EDC_UI_CONNECTOR_NAME", "edc.connector.name")))
                .body("EDC_UI_IDS_ID", equalTo(missingMessage("EDC_UI_IDS_ID", "edc.ids.id")))
                .body("EDC_UI_IDS_TITLE", equalTo(missingMessage("EDC_UI_IDS_TITLE", "edc.ids.title")))
                .body("EDC_UI_IDS_DESCRIPTION", equalTo(missingMessage("EDC_UI_IDS_DESCRIPTION", "edc.ids.description")))
                .body("EDC_UI_CURATOR_URL", equalTo(missingMessage("EDC_UI_CURATOR_URL", "edc.ids.curator")))
                .body("EDC_UI_MAINTAINER_URL", equalTo(missingMessage("EDC_UI_MAINTAINER_URL", "edc.ids.maintainer")))
                .body("EDC_UI_DAPS_OAUTH_TOKEN_URL", equalTo(missingMessage("EDC_UI_DAPS_OAUTH_TOKEN_URL", "edc.oauth.token.url")))
                .body("EDC_UI_DAPS_OAUTH_JWKS_URL", equalTo(missingMessage("EDC_UI_DAPS_OAUTH_JWKS_URL", "edc.oauth.provider.jwks.url")));
    }

    private String missingMessage(String edcUiPropName, String contextPropName) {
        return String.format(
                "Unset %s in EDC backend is required for EDC UI property %s.",
                contextPropName,
                edcUiPropName
        );
    }
}
