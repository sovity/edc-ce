/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import de.sovity.edc.utils.config.utils.UrlPathUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Configuration Properties for the EDC:<br>
 * - Source-Of-Truth for all EDC Properties used in our CE<br>
 * - List of available properties for documentation<br>
 * - Ordered list of properties for defaulting before edc boot
 */
@SuppressWarnings("unused")
@UtilityClass
public class ConfigProps {
    public static final List<ConfigProp> ALL_CE_PROPS = new ArrayList<>();

    public static final ConfigProp MY_EDC_NETWORK_TYPE = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("my.edc.network.type")
        .description("Configuring EDCs for different environments. Available values are: %s".formatted(
            String.join(", ", NetworkType.ALL_NETWORK_TYPES)))
        .warnIfOverridden(true)
        .defaultValue(NetworkType.PRODUCTION)
    );

    /* Basic Configuration */

    public static final ConfigProp MY_EDC_PARTICIPANT_ID = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.participant.id")
        .description("Participant ID / Connector ID")
        .required(true)
    );

    public static final ConfigProp MY_EDC_TITLE = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.title")
        .description("Connector Title")
        .required(true)
    );

    public static final ConfigProp MY_EDC_DESCRIPTION = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.description")
        .description("Connector Description")
        .required(true)
    );

    public static final ConfigProp MY_EDC_CURATOR_URL = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.curator.url")
        .description("Curator URL")
        .required(true)
    );

    public static final ConfigProp MY_EDC_CURATOR_NAME = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.curator.name")
        .description("Curator Name")
        .required(true)
    );

    public static final ConfigProp MY_EDC_MAINTAINER_URL = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.maintainer.url")
        .description("Maintainer URL")
        .defaultValue("https://sovity.de")
    );

    public static final ConfigProp MY_EDC_MAINTAINER_NAME = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.maintainer.name")
        .description("Maintainer Name")
        .defaultValue("sovity GmbH")
    );

    public static final ConfigProp MY_EDC_FQDN = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.fqdn")
        .description("Fully Qualified Domain Name of where the Connector is hosted, e.g. my-connector.myorg.com")
        .requiredIf(props -> NetworkType.isProduction(props) || NetworkType.isLocalDemoDockerCompose(props))
        .defaultValueFn(props -> new NetworkTypeMatcher<String>(props).unitTest(() -> "localhost").orElseThrow())
    );

    public static final ConfigProp MY_EDC_JDBC_URL = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.jdbc.url")
        .description("PostgreSQL DB Connection: JDBC URL")
        .required(true)
    );

    public static final ConfigProp MY_EDC_JDBC_USER = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.jdbc.user")
        .description("PostgreSQL DB Connection: Username")
        .required(true)
    );

    public static final ConfigProp MY_EDC_JDBC_PASSWORD = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.jdbc.password")
        .description("PostgreSQL DB Connection: Password")
        .required(true)
    );

    public static final ConfigProp MY_EDC_VAULT_INIT_ENABLED = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.vault.init.enabled")
        .description("Enable Vault Initialization. Enabled by default if in-memory vault is active.")
        .defaultValue("true")
    );

    public static final ConfigProp MY_EDC_VAULT_INIT_ENTRIES_WILDCARD = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("my.edc.vault.init.entries.*")
        .description("Puts given value into the vault on startup. Replace '*' with the key you want to put into the vault.")
    );

    /* Auth */

    // EDC_API_AUTH_KEY: ApiKeyDefaultValue
    public static final ConfigProp EDC_API_AUTH_KEY = addCeProp(builder -> builder
        .category(Category.BASIC)
        .property("edc.api.auth.key")
        .description("Management API: API Key, provided with Header X-Api-Key.")
        .required(true)
        .defaultValue("ApiKeyDefaultValue")
        .warnIfUnset(true)
    );

    public static final ConfigProp MY_EDC_C2C_IAM_TYPE = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("my.edc.c2c.iam.type")
        .description("Type of Connector-to-Connector IAM / Authentication Mechanism used. " +
            "Available values are: 'daps-sovity', 'daps-omejdn', 'mock-iam'. Default: 'daps-sovity'")
        .warnIfOverridden(true)
        .defaultValue("daps-sovity")
    );

    public static final ConfigProp EDC_OAUTH_TOKEN_URL = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.token.url")
        .description("OAuth2 / DAPS: Token URL")
        .relevantIf(C2cIamType::isDaps)
        .required(true)
    );

    public static final ConfigProp EDC_OAUTH_PROVIDER_JWKS_URL = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.provider.jwks.url")
        .description("OAuth2 / DAPS: JWKS URL")
        .relevantIf(C2cIamType::isDaps)
        .required(true)
    );

    public static final ConfigProp EDC_OAUTH_CLIENT_ID = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.client.id")
        .description("OAuth2 / DAPS: Client ID. Defaults to Participant ID")
        .relevantIf(C2cIamType::isDaps)
        .defaultValueFn(MY_EDC_PARTICIPANT_ID::getRaw)
    );

    public static final ConfigProp EDC_KEYSTORE = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.keystore")
        .description("File-Based Vault: Keystore file (.jks)")
        .relevantIf(C2cIamType::isDaps)
        .required(true)
    );

    public static final ConfigProp EDC_KEYSTORE_PASSWORD = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.keystore.password")
        .description("File-Based Vault: Keystore password")
        .relevantIf(C2cIamType::isDaps)
        .required(true)
    );

    public static final ConfigProp EDC_OAUTH_CERTIFICATE_ALIAS = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.certificate.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Public Key. Default: '1'")
        .relevantIf(C2cIamType::isDaps)
        .defaultValue("1")
    );

    public static final ConfigProp EDC_OAUTH_PRIVATE_KEY_ALIAS = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.private.key.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Private Key. Default: '1'")
        .relevantIf(C2cIamType::isDaps)
        .defaultValue("1")
    );

    public static final ConfigProp EDC_OAUTH_PROVIDER_AUDIENCE = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.provider.audience")
        .description("OAuth2 / DAPS: Provider Audience")
        .relevantIf(C2cIamType::isDaps)
        .warnIfOverridden(true)
        .defaultValueFn(props -> {
            if (C2cIamType.isDapsOmejdn(props)) {
                return "idsc:IDS_CONNECTORS_ALL";
            }

            // daps-sovity
            return EDC_OAUTH_TOKEN_URL.getRaw(props);
        })
    );

    public static final ConfigProp EDC_OAUTH_ENDPOINT_AUDIENCE = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.oauth.endpoint.audience")
        .description("OAuth2 / DAPS: Endpoint Audience")
        .relevantIf(C2cIamType::isDaps)
        .warnIfOverridden(true)
        .defaultValue("idsc:IDS_CONNECTORS_ALL")
    );

    public static final ConfigProp EDC_AGENT_IDENTITY_KEY = addCeProp(builder -> builder
        .category(Category.C2C_IAM)
        .property("edc.agent.identity.key")
        .description("OAuth2 / DAPS: Agent Identity Key")
        .relevantIf(C2cIamType::isDaps)
        .warnIfOverridden(true)
        .defaultValueFn(props -> {
            if (C2cIamType.isDapsOmejdn(props)) {
                return "client_id";
            }

            // daps-sovity
            return "referringConnector";
        })
    );

    /* Advanced */

    public static final ConfigProp MY_EDC_FIRST_PORT = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("my.edc.first.port")
        .description("The first port of several ports to be used for the several API endpoints. " +
            "Useful when starting two EDCs on the host machine network / during tests")
        .warnIfOverridden(true)
        .defaultValue("11000")
    );
    public static final ConfigProp EDC_SERVER_DB_CONNECTION_POOL_SIZE = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.server.db.connection.pool.size")
        .description("Size of the Hikari Connection Pool")
        .defaultValue("10")
    );

    public static final ConfigProp EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.flyway.additional.migration.locations")
        .description("Coma-separated list of additional flyway migration scripts locations. Useful for DB Migration Tests in Unit Tests. " +
            "Need to be correct Flyway Migration Script Locations. " +
            "See https://flywaydb.org/documentation/configuration/parameters/locations")
        .warnIfOverridden(true)
    );

    public static final ConfigProp EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.server.db.connection.timeout.in.ms")
        .description("Sets the connection timeout for the datasource in milliseconds.")
        .defaultValue("5000")
    );

    public static final ConfigProp EDC_FLYWAY_REPAIR = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.flyway.repair")
        .description("(Deprecated) Attempts to fix the history when a migration fails. Only supported in older migration scripts.")
        .defaultValue("false")
        .warnIfOverridden(true)
    );

    public static final ConfigProp EDC_FLYWAY_CLEAN_ENABLE = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.flyway.clean.enable")
        .description(
            "Allows the deletion of the database. Goes in pair with edc.flyway.clean. Both options must be enabled for a clean to happen.")
        .defaultValue("false")
        .warnIfOverridden(true)
    );

    public static final ConfigProp EDC_FLYWAY_CLEAN = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.flyway.clean")
        .description(
            "Request the deletion of the database. Goes in pair with edc.flyway.clean.enable. Both options must be enabled for a clean to happen.")
        .defaultValue("false")
        .warnIfOverridden(true)
    );

    public static final ConfigProp EDC_WEB_REST_CORS_ENABLED = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.enabled")
        .description("Enable CORS")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("true")
    );

    public static final ConfigProp EDC_WEB_REST_CORS_HEADERS = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.headers")
        .description("CORS: Allowed Headers")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("origin,content-type,accept,authorization,X-Api-Key")
    );

    public static final ConfigProp EDC_WEB_REST_CORS_ORIGINS = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.origins")
        .description("CORS: Allowed Origins")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("*")
    );

    public static final ConfigProp EDC_BUILD_DATE = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.build.date")
        .description("Build Date, usually set via CI into a build arg into the built image")
        .defaultValue("Unknown Version")
    );

    public static final ConfigProp EDC_LAST_COMMIT_INFO = addCeProp(builder -> builder
        .category(Category.ADVANCED)
        .property("edc.last.commit.info")
        .description("Last Commit Info / Build Version, usually set via CI into a build arg into the built image")
        .defaultValue("Unknown Version")
    );

    /* Defaults of EDC Configuration */

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_NAME = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.name")
        .description("Ensures the EDC initializes the DataSource 'default' because it initializes all edc.datasource.* data sources.")
        .warnIfOverridden(true)
        .defaultValue("default")
    );

    public static final ConfigProp MY_EDC_VAULT_INIT_ENTRY_DEFAULT_DATASOURCE_JDBC_URL = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.vault.init.entries.edc.datasource.default.url")
        .description("Initializes Vault Value: edc.datasource.default.url")
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
    );

    public static final ConfigProp MY_EDC_VAULT_INIT_ENTRY_DEFAULT_DATASOURCE_JDBC_USER = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.vault.init.entries.edc.datasource.default.user")
        .description("Initializes Vault Value: edc.datasource.default.user")
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
    );

    public static final ConfigProp MY_EDC_VAULT_INIT_ENTRY_DEFAULT_DATASOURCE_JDBC_PASSWORD = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.vault.init.entries.edc.datasource.default.password")
        .description("Initializes Vault Value: edc.datasource.default.password")
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
    );

    public static final ConfigProp MY_EDC_PROTOCOL = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.protocol")
        .description("HTTP Protocol for when the EDC exposes its own URL for callbacks")
        .warnIfOverridden(true)
        .defaultValueFn(props -> NetworkType.isProduction(props) ? "https://" : "http://")
    );

    public static final ConfigProp MY_EDC_BASE_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.base.path")
        .description("Optional prefix to be added before all API paths")
        .warnIfOverridden(true)
        .defaultValue("/")
    );

    public static final ConfigProp WEB_HTTP_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.path")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api"))
    );

    public static final ConfigProp WEB_HTTP_PORT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.port")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 1))
    );

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.path")
        .description("API Group 'Management' contains API endpoints for EDC interaction and " +
            "should be protected from unauthorized access. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/management"))
    );

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PORT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.port")
        .description(
            "API Group 'Management' contains API endpoints for EDC interaction and " +
                "should be protected from unauthorized access. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 2))
    );

    public static final ConfigProp WEB_HTTP_PROTOCOL_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.path")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/dsp"))
    );

    public static final ConfigProp WEB_HTTP_PROTOCOL_PORT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.port")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 3))
    );

    public static final ConfigProp WEB_HTTP_CONTROL_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.path")
        .description("API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/control"))
    );

    public static final ConfigProp WEB_HTTP_CONTROL_PORT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.port")
        .description("API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 4))
    );

    public static final ConfigProp WEB_HTTP_PUBLIC_PATH = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.path")
        .description("API Group 'Public' contains public data plane API endpoints. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/public"))
    );

    public static final ConfigProp WEB_HTTP_PUBLIC_PORT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.port")
        .description("API Group 'Public' contains public data plane API endpoints. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 5))
    );

    public static final ConfigProp EDC_JSONLD_HTTPS_ENABLED = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.jsonld.https.enabled")
        .description("Required to be set since Eclipse EDC 0.2.1")
        .warnIfOverridden(true)
        .defaultValue("true")
    );

    public static final ConfigProp MY_EDC_NAME_KEBAB_CASE = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.name.kebab-case")
        .description("Deprecated. Prefer using %s instead".formatted(MY_EDC_PARTICIPANT_ID.getProperty()))
        .warnIfOverridden(true)
    );

    public static final ConfigProp EDC_CONNECTOR_NAME = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.connector.name")
        .description("Connector Name")
        .warnIfOverridden(true)
        .defaultValueFn(props -> firstNonNull(MY_EDC_PARTICIPANT_ID.getRaw(props), MY_EDC_NAME_KEBAB_CASE.getRaw(props)))
    );

    public static final ConfigProp EDC_PARTICIPANT_ID = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.participant.id")
        .description("Participant ID / Connector ID")
        .warnIfOverridden(true)
        .defaultValueFn(props -> firstNonNull(MY_EDC_PARTICIPANT_ID.getRaw(props), MY_EDC_NAME_KEBAB_CASE.getRaw(props)))
    );

    public static final ConfigProp EDC_HOSTNAME = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.hostname")
        .description("Same as %s".formatted(MY_EDC_FQDN.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_FQDN::getRaw)
    );

    public static final ConfigProp EDC_DSP_CALLBACK_ADDRESS = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.dsp.callback.address")
        .description("Full URL for the DSP callback address")
        .warnIfOverridden(true)
        .defaultValueFn(ConfigUtils::getProtocolApiUrl)
    );

    public static final ConfigProp EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.ui.management.api.url.shown.in.dashboard")
        .description("URL shown in the EDC UI for the management API. This might differ from the default " +
            "Management API URL if an auth proxy solution has been put between")
        .defaultValueFn(ConfigUtils::getManagementApiUrl)
    );

    public static final ConfigProp MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.datasource.placeholder.baseurl")
        .description("Base URL for the On Request asset datasource, as reachable by the data plane")
        .warnIfOverridden(true)
        .defaultValueFn(EDC_DSP_CALLBACK_ADDRESS::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_URL = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.url")
        .description("Default Datasource: JDBC URL. Prefer setting %s".formatted(MY_EDC_JDBC_URL.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_USER = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.user")
        .description("Default Datasource: Username. Prefer setting %s".formatted(MY_EDC_JDBC_USER.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_USER::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_PASSWORD = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.password")
        .description("Default Datasource: Password. Prefer setting %s".formatted(MY_EDC_JDBC_PASSWORD.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_PASSWORD::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_LOGGINGHOUSE_URL = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.logginghouse.url")
        .relevantIf(NetworkType::isProduction)
        .description("MDS Prod Variants Only: Logging House URL")
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_LOGGINGHOUSE_USER = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.logginghouse.user")
        .relevantIf(NetworkType::isProduction)
        .description("MDS Prod Variants Only: Logging House User")
        .defaultValueFn(MY_EDC_JDBC_USER::getRaw)
    );

    public static final ConfigProp EDC_DATASOURCE_LOGGINGHOUSE_PASSWORD = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.logginghouse.password")
        .relevantIf(NetworkType::isProduction)
        .description("MDS Prod Variants Only: Logging House Password")
        .defaultValueFn(MY_EDC_JDBC_PASSWORD::getRaw)
    );

    public static final ConfigProp EDC_VAULT = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.vault")
        .description("This file could contain an entry replacing the EDC_KEYSTORE ENV var, " +
            "but for some reason it is required, and EDC won't start up if it isn't configured." +
            "It is created in the Dockerfile")
        .relevantIf(NetworkType::isProduction)
    );

    private static ConfigProp addCeProp(Consumer<ConfigProp.ConfigPropBuilder> builderFn) {
        var builder = ConfigProp.builder();
        builderFn.accept(builder);
        var built = builder.build();

        // Register the property in the list of all available CE properties
        // Order matters here, as the property defaults are calculated in order
        built.also(ALL_CE_PROPS::add);
        return built;
    }

    public String firstNonNull(String... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static String plus(Map<String, String> props, ConfigProp prop, int add) {
        var raw = prop.getRaw(props);
        var result = Integer.parseInt(raw == null ? "0" : raw) + add;
        return String.valueOf(result);
    }

    @UtilityClass
    public static class NetworkType {
        public static final String PRODUCTION = "production";
        public static final String LOCAL_DEMO_DOCKER_COMPOSE = "local-demo-docker-compose";
        public static final String UNIT_TEST = "unit-test";
        public static final List<String> ALL_NETWORK_TYPES = List.of(PRODUCTION, LOCAL_DEMO_DOCKER_COMPOSE, UNIT_TEST);

        public static boolean isProduction(Map<String, String> props) {
            return NetworkType.PRODUCTION.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }

        public static boolean isLocalDemoDockerCompose(Map<String, String> props) {
            return NetworkType.LOCAL_DEMO_DOCKER_COMPOSE.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }

        public static boolean isUnitTest(Map<String, String> props) {
            return NetworkType.UNIT_TEST.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }
    }

    @UtilityClass
    public static class C2cIamType {
        public static final String DAPS_SOVITY = "daps-sovity";
        public static final String DAPS_OMEJDN = "daps-omejdn";
        public static final String MOCK_IAM = "mock-iam";
        public static final List<String> ALL_NETWORK_TYPES = List.of(DAPS_SOVITY, DAPS_OMEJDN, MOCK_IAM);

        public static boolean isDaps(Map<String, String> props) {
            return isDapsSovity(props) || isDapsOmejdn(props);
        }

        public static boolean isDapsSovity(Map<String, String> props) {
            return DAPS_SOVITY.equals(MY_EDC_C2C_IAM_TYPE.getRaw(props));
        }

        public static boolean isDapsOmejdn(Map<String, String> props) {
            return DAPS_OMEJDN.equals(MY_EDC_C2C_IAM_TYPE.getRaw(props));
        }

        public static boolean isMockIam(Map<String, String> props) {
            return MOCK_IAM.equals(MY_EDC_C2C_IAM_TYPE.getRaw(props));
        }
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    public static class NetworkTypeMatcher<T> {
        private final Map<String, String> props;
        private Supplier<T> production;
        private Supplier<T> localDemoDockerCompose;
        private Supplier<T> unitTest;

        public T orElse(Supplier<T> elseFn) {
            if (production != null && NetworkType.isProduction(props)) {
                return production.get();
            }

            if (localDemoDockerCompose != null && NetworkType.isLocalDemoDockerCompose(props)) {
                return localDemoDockerCompose.get();
            }

            if (unitTest != null && NetworkType.isUnitTest(props)) {
                return unitTest.get();
            }

            return elseFn.get();
        }

        public T orElseThrow() {
            return orElse(() -> {
                var msg = "Unhandled %s: %s".formatted(
                    MY_EDC_NETWORK_TYPE.getProperty(),
                    MY_EDC_NETWORK_TYPE.getRaw(props)
                );
                throw new IllegalArgumentException(msg);
            });
        }
    }

    @UtilityClass
    private static class Category {
        public static final String BASIC = "Basic Configuration";
        public static final String ADVANCED = "Advanced configuration";
        public static final String C2C_IAM = "Connector-to-Connector IAM";
        public static final String RAW_EDC_CONFIG_DEFAULTS = "EDC Config Defaults / Overrides";
    }
}
