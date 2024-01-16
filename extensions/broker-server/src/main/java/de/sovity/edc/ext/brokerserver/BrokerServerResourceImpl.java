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

import de.sovity.edc.ext.brokerserver.api.BrokerServerResource;
import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalConnectorInfo;
import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalOrganizationMetadataRequest;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageResult;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferCountResult;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageResult;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.services.api.AuthorityPortalConnectorMetadataApiService;
import de.sovity.edc.ext.brokerserver.services.api.AuthorityPortalOrganizationMetadataApiService;
import de.sovity.edc.ext.brokerserver.services.api.CatalogApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorDetailApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorListApiService;
import de.sovity.edc.ext.brokerserver.services.api.DataOfferDetailApiService;
import de.sovity.edc.ext.brokerserver.services.config.AdminApiKeyValidator;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * Implementation of {@link BrokerServerResource}
 */
@RequiredArgsConstructor
public class BrokerServerResourceImpl implements BrokerServerResource {
    private final DslContextFactory dslContextFactory;
    private final ConnectorApiService connectorApiService;
    private final ConnectorListApiService connectorListApiService;
    private final ConnectorDetailApiService connectorDetailApiService;
    private final CatalogApiService catalogApiService;
    private final DataOfferDetailApiService dataOfferDetailApiService;
    private final AdminApiKeyValidator adminApiKeyValidator;
    private final AuthorityPortalConnectorMetadataApiService authorityPortalConnectorMetadataApiService;
    private final AuthorityPortalOrganizationMetadataApiService authorityPortalOrganizationMetadataApiService;

    @Override
    public CatalogPageResult catalogPage(CatalogPageQuery query) {
        return dslContextFactory.transactionResult(dsl -> catalogApiService.catalogPage(dsl, query));
    }

    @Override
    public ConnectorPageResult connectorPage(ConnectorPageQuery query) {
        return dslContextFactory.transactionResult(dsl -> connectorListApiService.connectorListPage(dsl, query));
    }

    @Override
    public DataOfferDetailPageResult dataOfferDetailPage(DataOfferDetailPageQuery query) {
        return dslContextFactory.transactionResult(dsl -> dataOfferDetailApiService.dataOfferDetailPage(dsl, query));
    }

    @Override
    public ConnectorDetailPageResult connectorDetailPage(ConnectorDetailPageQuery query) {
        return dslContextFactory.transactionResult(dsl -> connectorDetailApiService.connectorDetailPage(dsl, query));
    }

    @Override
    public void addConnectors(List<String> endpoints, String adminApiKey) {
        adminApiKeyValidator.validateAdminApiKey(adminApiKey);
        dslContextFactory.transaction(dsl -> connectorApiService.addConnectors(dsl, endpoints));
    }

    @Override
    public void deleteConnectors(List<String> endpoints, String adminApiKey) {
        adminApiKeyValidator.validateAdminApiKey(adminApiKey);
        dslContextFactory.transaction(dsl -> connectorApiService.deleteConnectors(dsl, endpoints));
    }

    @Override
    public List<AuthorityPortalConnectorInfo> getConnectorMetadata(List<String> endpoints, String adminApiKey) {
        adminApiKeyValidator.validateAdminApiKey(adminApiKey);
        return dslContextFactory.transactionResult(dsl -> authorityPortalConnectorMetadataApiService.getMetadataByEndpoints(dsl, endpoints));
    }

    @Override
    public DataOfferCountResult dataOfferCount(List<String> endpoints) {
        return dslContextFactory.transactionResult(dsl -> authorityPortalConnectorMetadataApiService.countByEndpoints(dsl, endpoints));
    }

    @Override
    public void setOrganizationMetadata(AuthorityPortalOrganizationMetadataRequest organizationMetadataRequest, String adminApiKey) {
        adminApiKeyValidator.validateAdminApiKey(adminApiKey);
        dslContextFactory.transaction(dsl -> authorityPortalOrganizationMetadataApiService.setOrganizationMetadata(dsl, organizationMetadataRequest.getOrganizations()));
    }
}
