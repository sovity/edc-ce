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

package de.sovity.edc.client.examples;

import de.sovity.edc.client.EdcClient;
import io.quarkus.runtime.configuration.ConfigUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class EdcClientSetup {
    private static final Logger LOG = Logger.getLogger(EdcClientSetup.class.getName());

    @ConfigProperty(name = "my-edc.management-api-url")
    String managementApiUrl;

    @ConfigProperty(name = "my-edc.management-api-key")
    String managementApiKey;

    @Produces
    @ApplicationScoped
    public EdcClient buildEdcClient() {
        var client = EdcClient.builder()
                .managementApiUrl(managementApiUrl)
                .managementApiKey(managementApiKey)
                .build();
        testEdcConnection(client);
        return client;
    }

    private void testEdcConnection(EdcClient client) {
        if (ConfigUtils.getProfiles().contains("test")) {
            LOG.info("Skipping EDC connection test.");
            return;
        }

        client.testConnection();
        LOG.info("Successfully connected to EDC Connector %s.".formatted(managementApiUrl));
    }
}
