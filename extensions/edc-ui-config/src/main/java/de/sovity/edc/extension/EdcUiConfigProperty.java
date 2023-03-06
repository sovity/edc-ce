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

package de.sovity.edc.extension;

import static java.util.Objects.requireNonNull;

/**
 * List of EDC UI Properties that are explicitly handled by this extension.
 */
public enum EdcUiConfigProperty {
    CONNECTOR_ENDPOINT("EDC_UI_CONNECTOR_ENDPOINT", null),
    CONNECTOR_NAME("EDC_UI_CONNECTOR_NAME", "edc.connector.name"),
    IDS_ID("EDC_UI_IDS_ID", "edc.ids.id"),
    IDS_TITLE("EDC_UI_IDS_TITLE", "edc.ids.title"),
    IDS_DESCRIPTION("EDC_UI_IDS_DESCRIPTION", "edc.ids.description"),
    CURATOR_URL("EDC_UI_CURATOR_URL", "edc.ids.curator"),
    MAINTAINER_URL("EDC_UI_MAINTAINER_URL", "edc.ids.maintainer"),
    DAPS_OAUTH_TOKEN_URL("EDC_UI_DAPS_OAUTH_TOKEN_URL", "edc.oauth.token.url"),
    DAPS_OAUTH_JWKS_URL("EDC_UI_DAPS_OAUTH_JWKS_URL", "edc.oauth.provider.jwks.url");

    /**
     * The property name to be sent to EDC UI.
     */
    private final String uiName;

    /**
     * The config property name in EDC Backend.
     * <br>
     * If non-null, value is taken from this config property.
     * <br>
     * If null, value is expected to be built manually.
     */
    private final String backendNameOrNull;

    EdcUiConfigProperty(String uiName, String backendNameOrNull) {
        this.uiName = requireNonNull(uiName, "edcUiPropertyName");
        this.backendNameOrNull = backendNameOrNull;
    }

    /**
     * See @link {@link #uiName}
     *
     * @return See @link {@link #uiName}
     */
    public String getUiName() {
        return uiName;
    }


    /**
     * See {@link #backendNameOrNull}
     *
     * @return {@link #backendNameOrNull}
     */
    public String getBackendNameOrNull() {
        return backendNameOrNull;
    }
}
