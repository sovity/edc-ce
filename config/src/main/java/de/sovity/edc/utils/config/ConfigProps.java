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
import java.util.function.Supplier;

/**
 * Configuration Properties for the EDC:<br>
 * - Source-Of-Truth for all EDC Properties used in our code<br>
 * - Ordered list of available properties for documentation<br>
 * - Ordered list of properties for defaulting before edc boot
 */
@SuppressWarnings("unused")
@UtilityClass
public class ConfigProps {
    public static final List<ConfigProp> ALL_CE_PROPS = new ArrayList<>();

    /* The order of the properties affects evaluation! */

    public static final ConfigProp MY_EDC_NETWORK_TYPE = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("my.edc.network.type")
        .description(
            "Configuring EDCs for different environments. Available values are: %s".formatted(
                String.join(", ", NetworkType.ALL_NETWORK_TYPES)))
        .warnIfOverridden(true)
        .defaultValue(NetworkType.PRODUCTION)
        .build().also(ALL_CE_PROPS::add);

    /* Basic Configuration */

    public static final ConfigProp MY_EDC_PARTICIPANT_ID = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.participant.id")
        .description("Participant ID / Connector ID")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_TITLE = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.title")
        .description("Connector Title")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_DESCRIPTION = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.description")
        .description("Connector Description")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_CURATOR_URL = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.curator.url")
        .description("Curator URL")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_CURATOR_NAME = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.curator.name")
        .description("Curator Name")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_MAINTAINER_URL = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.maintainer.url")
        .description("Maintainer URL")
        .defaultValue("https://sovity.de")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_MAINTAINER_NAME = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.maintainer.name")
        .description("Maintainer Name")
        .defaultValue("sovity GmbH")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_FQDN = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.fqdn")
        .description("Fully Qualified Domain Name of where the Connector is hosted, e.g. my-connector.myorg.com")
        .requiredIf(props -> NetworkType.isProduction(props) || NetworkType.isLocalDemoDockerCompose(props))
        .defaultValueFn(props -> new NetworkTypeMatcher<String>(props).unitTest(() -> "localhost").orElseThrow())
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_JDBC_URL = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.jdbc.url")
        .description("PostgreSQL DB Connection: JDBC URL")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_JDBC_USER = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.jdbc.user")
        .description("PostgreSQL DB Connection: Username")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_JDBC_PASSWORD = ConfigProp.builder()
        .category(Category.BASIC)
        .property("my.edc.jdbc.password")
        .description("PostgreSQL DB Connection: Password")
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    /* Auth */

    // EDC_API_AUTH_KEY: ApiKeyDefaultValue
    public static final ConfigProp EDC_API_AUTH_KEY = ConfigProp.builder()
        .category(Category.BASIC)
        .property("edc.api.auth.key")
        .description("Management API: API Key, provided with Header X-Api-Key.")
        .required(true)
        .defaultValue("ApiKeyDefaultValue")
        .warnIfUnset(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_C2C_IAM_TYPE = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("my.edc.c2c.iam.type")
        .description(
            "Type of Connector-to-Connector IAM / Authentication Mechanism used. Available values are: 'daps-sovity', 'daps-omejdn', 'mock-iam'. Default: 'daps-sovity'")
        .warnIfOverridden(true)
        .defaultValue("daps-sovity")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_TOKEN_URL = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.token.url")
        .description("OAuth2 / DAPS: Token URL")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_PROVIDER_JWKS_URL = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.provider.jwks.url")
        .description("OAuth2 / DAPS: JWKS URL")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_CLIENT_ID = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.client.id")
        .description("OAuth2 / DAPS: Client ID. Defaults to Participant ID")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .defaultValueFn(MY_EDC_PARTICIPANT_ID::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_KEYSTORE = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.keystore")
        .description("File-Based Vault: Keystore file (.jks)")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_KEYSTORE_PASSWORD = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.keystore.password")
        .description("File-Based Vault: Keystore password")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .required(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_CERTIFICATE_ALIAS = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.certificate.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Public Key. Default: '1'")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .defaultValue("1")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_PRIVATE_KEY_ALIAS = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.private.key.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Private Key. Default: '1'")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .defaultValue("1")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_PROVIDER_AUDIENCE = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.provider.audience")
        .description("OAuth2 / DAPS: Provider Audience")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .warnIfOverridden(true)
        .defaultValueFn(props -> {
            if ("daps-omejdn".equals(MY_EDC_C2C_IAM_TYPE.getRaw(props))) {
                return "idsc:IDS_CONNECTORS_ALL";
            }

            // daps-sovity
            return EDC_OAUTH_TOKEN_URL.getRaw(props);
        })
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_OAUTH_ENDPOINT_AUDIENCE = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.oauth.endpoint.audience")
        .description("OAuth2 / DAPS: Endpoint Audience")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .warnIfOverridden(true)
        .defaultValue("idsc:IDS_CONNECTORS_ALL")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_AGENT_IDENTITY_KEY = ConfigProp.builder()
        .category(Category.C2C_IAM)
        .property("edc.agent.identity.key")
        .description("OAuth2 / DAPS: Agent Identity Key")
        .relevantIf(props -> MY_EDC_C2C_IAM_TYPE.getRaw(props).startsWith("daps"))
        .warnIfOverridden(true)
        .defaultValueFn(props -> {
            if ("daps-omejdn".equals(MY_EDC_C2C_IAM_TYPE.getRaw(props))) {
                return "client_id";
            }

            // daps-sovity
            return "referringConnector";
        })
        .build().also(ALL_CE_PROPS::add);

    /* Advanced */

    public static final ConfigProp MY_EDC_FIRST_PORT = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("my.edc.first.port")
        .description("The first port of several ports to be used for the several API endpoints. " +
            "Useful when starting two EDCs on the host machine network / during tests")
        .warnIfOverridden(true)
        .defaultValue("11000")
        .build().also(ALL_CE_PROPS::add);
    public static final ConfigProp EDC_SERVER_DB_CONNECTION_POOL_SIZE = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.server.db.connection.pool.size")
        .description("Size of the Hikari Connection Pool")
        .defaultValue("10")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.flyway.additional.migration.locations")
        .description("Coma-separated list of additional flyway migration scripts locations. Useful for DB Migration Tests in Unit Tests. " +
            "Need to be correct Flyway Migration Script Locations. " +
            "See https://flywaydb.org/documentation/configuration/parameters/locations")
        .warnIfOverridden(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.server.db.connection.timeout.in.ms")
        .description("Sets the connection timeout for the datasource in milliseconds.")
        .defaultValue("5000")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_FLYWAY_REPAIR = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.flyway.repair")
        .description("(Deprecated) Attempts to fix the history when a migration fails. Only supported in older migration scripts.")
        .defaultValue("false")
        .warnIfOverridden(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_FLYWAY_CLEAN_ENABLE = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.flyway.clean.enable")
        .description("Allows the deletion of the database. Goes in pair with edc.flyway.clean.enable. Both options must be enabled for a clean to happen.")
        .defaultValue("false")
        .warnIfOverridden(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_FLYWAY_CLEAN = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.flyway.clean")
        .description("Request the deletion of the database. Goes in pair with edc.flyway.clean. Both options must be enabled for a clean to happen.")
        .defaultValue("false")
        .warnIfOverridden(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_WEB_REST_CORS_ENABLED = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.enabled")
        .description("Enable CORS")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("true")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_WEB_REST_CORS_HEADERS = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.headers")
        .description("CORS: Allowed Headers")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("origin,content-type,accept,authorization,X-Api-Key")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_WEB_REST_CORS_ORIGINS = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.origins")
        .description("CORS: Allowed Origins")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("*")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_BUILD_DATE = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.build.date")
        .description("Build Date, usually set via CI into a build arg into the built image")
        .defaultValue("Unknown Version")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_LAST_COMMIT_INFO = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.build.date")
        .description("Last Commit Info / Build Version, usually set via CI into a build arg into the built image")
        .defaultValue("Unknown Version")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_DOCKER_COMPOSE_SERVICE_NAME = ConfigProp.builder()
        .category(Category.ADVANCED)
        .property("edc.web.rest.cors.origins")
        .description("CORS: Allowed Origins")
        .warnIfOverridden(true)
        .relevantIf(props -> !NetworkType.isProduction(props))
        .defaultValue("*")
        .build().also(ALL_CE_PROPS::add);

    /* Defaults of EDC Configuration */

    public static final ConfigProp MY_EDC_PROTOCOL = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.protocol")
        .description("HTTP Protocol for when the EDC exposes its own URL for callbacks")
        .warnIfOverridden(true)
        .defaultValueFn(props -> NetworkType.isProduction(props) ? "https://" : "http://")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_BASE_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.base.path")
        .description("Optional prefix to be added before all API paths")
        .warnIfOverridden(true)
        .defaultValue("/")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.path")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api"))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PORT = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.port")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 1))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.path")
        .description(
            "API Group 'Management' contains API endpoints for EDC interaction and should be protected from unauthorized access. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/management"))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PORT = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.port")
        .description(
            "API Group 'Management' contains API endpoints for EDC interaction and should be protected from unauthorized access. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 2))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PROTOCOL_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.path")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/dsp"))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PROTOCOL_PORT = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.port")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 3))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_CONTROL_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.path")
        .description(
            "API Group 'Control' contains API endpoints for control plane/data plane interaction and should be non-public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/control"))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_CONTROL_PORT = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.port")
        .description(
            "API Group 'Control' contains API endpoints for control plane/data plane interaction and should be non-public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 4))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PUBLIC_PATH = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.path")
        .description("API Group 'Public' contains public data plane API endpoints. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/public"))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp WEB_HTTP_PUBLIC_PORT = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.port")
        .description("API Group 'Public' contains public data plane API endpoints. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 5))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_JSONLD_HTTPS_ENABLED = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.jsonld.https.enabled")
        .description("Required to be set since Eclipse EDC 0.2.1")
        .warnIfOverridden(true)
        .defaultValue("true")
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_NAME_KEBAB_CASE = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.name.kebab-case")
        .description("Deprecated. Prefer using %s instead".formatted(MY_EDC_PARTICIPANT_ID.getProperty()))
        .warnIfOverridden(true)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_CONNECTOR_NAME = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.connector.name")
        .description("Connector Name")
        .warnIfOverridden(true)
        .defaultValueFn(props -> coalesce(MY_EDC_PARTICIPANT_ID.getRaw(props), MY_EDC_NAME_KEBAB_CASE.getRaw(props)))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_PARTICIPANT_ID = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.participant.id")
        .description("Participant ID / Connector ID")
        .warnIfOverridden(true)
        .defaultValueFn(props -> coalesce(MY_EDC_PARTICIPANT_ID.getRaw(props), MY_EDC_NAME_KEBAB_CASE.getRaw(props)))
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_HOSTNAME = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.hostname")
        .description("Same as %s".formatted(MY_EDC_FQDN.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_FQDN::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_DSP_CALLBACK_ADDRESS = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.dsp.callback.address")
        .description("Full URL for the DSP callback address")
        .warnIfOverridden(true)
        .defaultValueFn(ConfigUtils::getProtocolApiUrl)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.ui.management.api.url.shown.in.dashboard")
        .description(
            "URL shown in the EDC UI for the management API. This might differ from the default Management API URL if an auth proxy solution has been put between")
        .defaultValueFn(ConfigUtils::getManagementApiUrl)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.datasource.placeholder.baseurl")
        .description("Base URL for the On Request asset datasource, as reachable by the data plane")
        .warnIfOverridden(true)
        .defaultValueFn(EDC_DSP_CALLBACK_ADDRESS::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_URL = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.url")
        .description("Default Datasource: JDBC URL. Prefer setting %s".formatted(MY_EDC_JDBC_URL.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_URL::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_USER = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.user")
        .description("Default Datasource: Username. Prefer setting %s".formatted(MY_EDC_JDBC_USER.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_USER::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public static final ConfigProp EDC_DATASOURCE_DEFAULT_PASSWORD = ConfigProp.builder()
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.datasource.default.password")
        .description("Default Datasource: Password. Prefer setting %s".formatted(MY_EDC_JDBC_PASSWORD.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_JDBC_PASSWORD::getRaw)
        .build().also(ALL_CE_PROPS::add);

    public String coalesce(String... values) {
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
