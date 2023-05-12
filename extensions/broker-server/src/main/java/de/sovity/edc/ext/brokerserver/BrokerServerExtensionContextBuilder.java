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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.dao.stores.ConnectorStore;
import de.sovity.edc.ext.brokerserver.dao.stores.ContractOfferStore;
import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.api.CatalogApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorApiService;
import de.sovity.edc.ext.brokerserver.services.api.PaginationMetadataUtils;
import lombok.NoArgsConstructor;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.configuration.Config;


/**
 * Manual Dependency Injection.
 * <p>
 * We want to develop as Java Backend Development is done, but we have
 * no CDI / DI Framework to rely on.
 * <p>
 * EDC {@link Inject} only works in {@link BrokerServerExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BrokerServerExtensionContextBuilder {
    public static BrokerServerExtensionContext buildContext(Config config) {
        // Dao
        var connectorStore = new ConnectorStore();
        var contractOfferStore = new ContractOfferStore();

        // Services
        var brokerServerInitializer = new BrokerServerInitializer(connectorStore, config);

        // UI Capabilities
        var paginationMetadataUtils = new PaginationMetadataUtils();
        var catalogApiService = new CatalogApiService(
                contractOfferStore,
                paginationMetadataUtils
        );
        var connectorApiService = new ConnectorApiService(
                connectorStore,
                paginationMetadataUtils
        );
        var brokerServerResource = new BrokerServerResourceImpl(connectorApiService, catalogApiService);
        return new BrokerServerExtensionContext(brokerServerResource, brokerServerInitializer);
    }
}
