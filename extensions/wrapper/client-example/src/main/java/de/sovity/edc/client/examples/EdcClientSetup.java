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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class EdcClientSetup {
    private static final Logger LOG = Logger.getLogger(EdcClientSetup.class.getName());

    @ConfigProperty(name = "client-example.management-api-url")
    String managementApiUrl;

    @ConfigProperty(name = "client-example.management-api-key")
    String managementApiKey;

    @ConfigProperty(name = "client-example.test-connection")
    Boolean testConnection;

    @Produces
    @ApplicationScoped
    public EdcClient buildEdcClient() {
        EdcClient client = EdcClient.builder()
                .managementApiUrl(managementApiUrl)
                .managementApiKey(managementApiKey)
                .build();
        testEdcConnection(client);
        return client;
    }

    private void testEdcConnection(EdcClient client) {
        if (Boolean.TRUE.equals(testConnection)) {
            LOG.info("Testing EDC connection...");
            client.testConnection();
            LOG.info("Successfully connected to EDC.");
        } else {
            LOG.info("Skipping EDC connection test.");
        }
    }
}
