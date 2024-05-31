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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.client.gen.model.AuthorityPortalOrganizationMetadata;
import de.sovity.edc.ext.brokerserver.client.gen.model.AuthorityPortalOrganizationMetadataRequest;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.OrganizationMetadataRecord;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.ADMIN_API_KEY;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class AuthorityPortalOrganizationMetadataApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
    }

    @Test
    void testSetOrganizationMetadata() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            createOrgMetadataInDb(dsl, "MDSL1111AA", "Test Org A");
            createOrgMetadataInDb(dsl, "MDSL2222BB", "Test Org B");
            createOrgMetadataInDb(dsl, "MDSL3333CC", "Test Org C");

            // act
            var orgMetadataRequest = new AuthorityPortalOrganizationMetadataRequest();
            orgMetadataRequest.setOrganizations(List.of(
                buildOrgMetadataRequestEntry("MDSL2222BB", "Test Org B"),
                buildOrgMetadataRequestEntry("MDSL3333CC", "Test Org C new"),
                buildOrgMetadataRequestEntry("MDSL4444DD", "Test Org D")
            ));

            brokerServerClient().brokerServerApi().setOrganizationMetadata(
                ADMIN_API_KEY,
                orgMetadataRequest
            );

            // assert
            var orgMetadata = getOrgMetadataFromDb(dsl);
            assertThat(orgMetadata).hasSize(3);
            assertThat(orgMetadata).extracting(OrganizationMetadataRecord::getName).containsExactlyInAnyOrder("Test Org B", "Test Org C new", "Test Org D");
        });
    }

    @Test
    void testSetEmptyOrganizationMetadata() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            createOrgMetadataInDb(dsl, "MDSL1111AA", "Test Org A");

            // act
            var orgMetadataRequest = new AuthorityPortalOrganizationMetadataRequest();
            orgMetadataRequest.setOrganizations(List.of());

            brokerServerClient().brokerServerApi().setOrganizationMetadata(
                ADMIN_API_KEY,
                orgMetadataRequest
            );

            // assert
            var orgMetadata = getOrgMetadataFromDb(dsl);
            assertThat(orgMetadata).isEmpty();
        });
    }

    private void createOrgMetadataInDb(DSLContext dsl, String mdsId, String name) {
        var organizationMetadata = dsl.newRecord(Tables.ORGANIZATION_METADATA);
        organizationMetadata.setMdsId(mdsId);
        organizationMetadata.setName(name);
        organizationMetadata.insert();
    }

    private List<OrganizationMetadataRecord> getOrgMetadataFromDb(DSLContext dsl) {
        return dsl.selectFrom(Tables.ORGANIZATION_METADATA).fetch();
    }

    private AuthorityPortalOrganizationMetadata buildOrgMetadataRequestEntry(String mdsId, String name) {
        var orgMetadata = new AuthorityPortalOrganizationMetadata();
        orgMetadata.setMdsId(mdsId);
        orgMetadata.setName(name);
        return orgMetadata;
    }
}
