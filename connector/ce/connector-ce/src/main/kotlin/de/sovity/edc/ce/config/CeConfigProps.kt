/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.config

import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.runtime.modules.model.ConfigPropRef

@Suppress("MaxLineLength", "LargeClass")
object CeConfigProps {
    @JvmStatic
    val SOVITY_BUILD_DATE = ConfigPropRef(
        property = "sovity.build.date",
        defaultDocumentation = """
            Indicates when this version of the EDC was built.
            This will be set by the CI. It could be optionally overridden.
        """.trimIndent()
    )

    @JvmStatic
    val SOVITY_BUILD_VERSION = ConfigPropRef(
        property = "sovity.build.version",
        defaultDocumentation = """
            Indicates the build version.
            Contains the sovity EE version or last version + commit hash of the image.
        """.trimIndent()
    )

    @JvmStatic
    val SOVITY_DEPLOYMENT_KIND = ConfigPropRef(
        property = "sovity.deployment.kind",
        defaultDocumentation = "Choose between different EDC deployment configurations"
    )

    @JvmStatic
    val SOVITY_DATASPACE_KIND = ConfigPropRef(
        property = "sovity.dataspace.kind",
        defaultDocumentation = "Configures the dataspace for the given dataspace, deciding C2C IAM, policies, features and integrations",
    )

    @JvmStatic
    val SOVITY_MANAGEMENT_API_IAM_KIND = ConfigPropRef(
        property = "sovity.management.api.iam.kind",
        defaultDocumentation = "Type of authentication the Management API gets secured with."
    )

    @JvmStatic
    val SOVITY_EDC_TITLE = ConfigPropRef(
        property = "sovity.edc.title",
        defaultDocumentation = "Connector Title"
    )

    @JvmStatic
    val SOVITY_EDC_DESCRIPTION = ConfigPropRef(
        property = "sovity.edc.description",
        defaultDocumentation = "Connector Description"
    )

    @JvmStatic
    val SOVITY_EDC_CURATOR_URL = ConfigPropRef(
        property = "sovity.edc.curator.url",
        defaultDocumentation = "Curator URL"
    )

    @JvmStatic
    val SOVITY_EDC_CURATOR_NAME = ConfigPropRef(
        property = "sovity.edc.curator.name",
        defaultDocumentation = "Curator Name"
    )

    @JvmStatic
    val SOVITY_EDC_MAINTAINER_URL = ConfigPropRef(
        property = "sovity.edc.maintainer.url",
        defaultDocumentation = "Maintainer URL"
    )

    @JvmStatic
    val SOVITY_EDC_MAINTAINER_NAME = ConfigPropRef(
        property = "sovity.edc.maintainer.name",
        defaultDocumentation = "Maintainer Name"
    )

    @JvmStatic
    val SOVITY_EDC_FQDN_PUBLIC = ConfigPropRef(
        property = "sovity.edc.fqdn.public",
        defaultDocumentation = "Fully Qualified Domain Name for the Connector Public APIs, especially the Protocol API, e.g. `my-connector.myorg.com`"
    )

    @JvmStatic
    val SOVITY_EDC_FQDN_INTERNAL = ConfigPropRef(
        property = "sovity.edc.fqdn.internal",
        defaultDocumentation = "Fully Qualified Domain Name for the Connector private APIs. Used for cp/dp communication via the control api"
    )

    @JvmStatic
    val SOVITY_DB_CONNECTION_POOL_SIZE = ConfigPropRef(
        property = "sovity.db.connection.pool.size",
        defaultDocumentation = "Size of the DB Connection Pool",
    )

    @JvmStatic
    val SOVITY_FLYWAY_MIGRATION_LOCATION = ConfigPropRef(
        property = "sovity.flyway.migration.location",
        defaultDocumentation = "Main location of migrations. Is a property to prevent clashes when having CE migrations in the classpath in the EE. " +
            "Needs to be a correct Flyway Migration Script Location. " +
            "See https://flywaydb.org/documentation/configuration/parameters/locations",
    )

    @JvmStatic
    val SOVITY_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS = ConfigPropRef(
        property = "sovity.flyway.additional.migration.locations",
        defaultDocumentation = "Coma-separated list of additional flyway migration scripts locations. Useful for DB Migration Tests in Unit Tests. " +
            "Need to be correct Flyway Migration Script Locations. " +
            "See https://flywaydb.org/documentation/configuration/parameters/locations",
    )

    @JvmStatic
    val SOVITY_JDBC_URL = ConfigPropRef(
        property = "sovity.jdbc.url",
        defaultDocumentation = "PostgreSQL DB Connection: JDBC URL",
    )

    @JvmStatic
    val SOVITY_JDBC_USER = ConfigPropRef(
        property = "sovity.jdbc.user",
        defaultDocumentation = "PostgreSQL DB Connection: Username",
    )

    @JvmStatic
    val SOVITY_JDBC_PASSWORD = ConfigPropRef(
        property = "sovity.jdbc.password",
        defaultDocumentation = "PostgreSQL DB Connection: Password",
    )

    @JvmStatic
    val SOVITY_FLYWAY_CLEAN_ENABLE = ConfigPropRef(
        property = "sovity.flyway.clean.enable",
        defaultDocumentation = "Allows the deletion of the database. Goes in pair with edc.flyway.clean. Both options must be enabled for a clean to happen.",
    )

    @JvmStatic
    val SOVITY_FLYWAY_CLEAN = ConfigPropRef(
        property = "sovity.flyway.clean",
        defaultDocumentation = "Request the deletion of the database. Goes in pair with edc.flyway.clean.enable. Both options must be enabled for a clean to happen.",
    )

    @JvmStatic
    val SOVITY_HTTP_PROTOCOL = ConfigPropRef(
        property = "sovity.http.protocol",
        defaultDocumentation = "HTTP Protocol for when the EDC exposes its own URL for callbacks. Configurable due to different environments, e.g. test or docker compose won't use TLS, but production will",
    )

    @JvmStatic
    val EDC_HTTP_CLIENT_HTTPS_ENFORCE = ConfigPropRef(
        property = "edc.http.client.https.enforce",
        defaultDocumentation = "OkHttpClient: If true, enable HTTPS call enforcement",
    )

    @JvmStatic
    val SOVITY_BASE_PATH = ConfigPropRef(
        property = "sovity.base.path",
        defaultDocumentation = "Optional prefix to be added before all API paths",
    )

    @JvmStatic
    val SOVITY_DATA_ADDRESS_TYPES = ConfigPropRef(
        property = "sovity.data.address.types",
        defaultDocumentation = "Data Source and Data Sink Types",
    )

    @JvmStatic
    val SOVITY_VAULT_KIND = ConfigPropRef(
        property = "sovity.vault.kind",
        defaultDocumentation = "Which vault the EDC deployment should use",
    )

    @JvmStatic
    val SOVITY_VAULT_IN_MEMORY_INIT_WILDCARD = ConfigPropRef(
        property = "sovity.vault.in-memory.init.*",
        defaultDocumentation = "In-Memory Vault: Inits the vault with all config properties starting with this key.",
    )

    @JvmStatic
    val EDC_API_AUTH_KEY = ConfigPropRef(
        property = "edc.api.auth.key",
        defaultDocumentation = "Management API: API Key, provided with Header X-Api-Key.",
    )

    @JvmStatic
    val EDC_OAUTH_TOKEN_URL = ConfigPropRef(
        property = "edc.oauth.token.url",
        defaultDocumentation = "OAuth2 / DAPS: Token URL",
    )

    @JvmStatic
    val EDC_OAUTH_PROVIDER_JWKS_URL = ConfigPropRef(
        property = "edc.oauth.provider.jwks.url",
        defaultDocumentation = "OAuth2 / DAPS: JWKS URL",
    )

    @JvmStatic
    val EDC_OAUTH_CLIENT_ID = ConfigPropRef(
        property = "edc.oauth.client.id",
        defaultDocumentation = "This property is both used by DAPS and SSI. See the modules for the documentation.",
    )

    @JvmStatic
    val EDC_OAUTH_CERTIFICATE_ALIAS = ConfigPropRef(
        property = "edc.oauth.certificate.alias",
        defaultDocumentation = "OAuth2 / DAPS: Vault Entry: ${CeVaultEntries.DAPS_CERT.documentation}",
    )

    @JvmStatic
    val EDC_OAUTH_PRIVATE_KEY_ALIAS = ConfigPropRef(
        property = "edc.oauth.private.key.alias",
        defaultDocumentation = "OAuth2 / DAPS: Vault Entry: ${CeVaultEntries.DAPS_PRIV.documentation}",
    )

    @JvmStatic
    val EDC_OAUTH_PROVIDER_AUDIENCE = ConfigPropRef(
        property = "edc.oauth.provider.audience",
        defaultDocumentation = "OAuth2 / DAPS: Provider Audience",
    )

    @JvmStatic
    val EDC_OAUTH_ENDPOINT_AUDIENCE = ConfigPropRef(
        property = "edc.oauth.endpoint.audience",
        defaultDocumentation = "OAuth2 / DAPS: Endpoint Audience",
    )

    @JvmStatic
    val EDC_AGENT_IDENTITY_KEY = ConfigPropRef(
        property = "edc.agent.identity.key",
        defaultDocumentation = "OAuth2 / DAPS: Access token claim name that must coincide with the Participant ID",
    )

    @JvmStatic
    val EDC_WEB_REST_CORS_ENABLED = ConfigPropRef(
        property = "edc.web.rest.cors.enabled",
        defaultDocumentation = "Enable CORS",
    )

    @JvmStatic
    val EDC_WEB_REST_CORS_HEADERS = ConfigPropRef(
        property = "edc.web.rest.cors.headers",
        defaultDocumentation = "CORS: Allowed Headers",
    )

    @JvmStatic
    val EDC_WEB_REST_CORS_ORIGINS = ConfigPropRef(
        property = "edc.web.rest.cors.origins",
        defaultDocumentation = "CORS: Allowed Origins",
    )

    @JvmStatic
    val EDC_TRANSFER_SEND_RETRY_LIMIT = ConfigPropRef(
        property = "edc.transfer.send.retry.limit",
        defaultDocumentation = "Transfer Process Retry Limit",
    )

    @JvmStatic
    val EDC_TRANSFER_SEND_RETRY_BASE_DELAY_MS = ConfigPropRef(
        property = "edc.transfer.send.retry.base-delay.ms",
        defaultDocumentation = "Transfer Process Retry Base Delay in Milliseconds",
    )

    @JvmStatic
    val EDC_NEGOTIATION_CONSUMER_SEND_RETRY_LIMIT = ConfigPropRef(
        property = "edc.negotiation.consumer.send.retry.limit",
        defaultDocumentation = "Contract Negotiation Retry Limit (Consuming Side)",
    )

    @JvmStatic
    val EDC_NEGOTIATION_PROVIDER_SEND_RETRY_LIMIT = ConfigPropRef(
        property = "edc.negotiation.provider.send.retry.limit",
        defaultDocumentation = "Contract Negotiation Retry Limit (Providing Side)",
    )

    @JvmStatic
    val EDC_NEGOTIATION_CONSUMER_SEND_RETRY_BASE_DELAY_MS = ConfigPropRef(
        property = "edc.negotiation.consumer.send.retry.base-delay.ms",
        defaultDocumentation = "Contract Negotiation Retry Base Delay in Milliseconds (Consuming Side)",
    )

    @JvmStatic
    val EDC_NEGOTIATION_PROVIDER_SEND_RETRY_BASE_DELAY_MS = ConfigPropRef(
        property = "edc.negotiation.provider.send.retry.base-delay.ms",
        defaultDocumentation = "Contract Negotiation Retry Base Delay in Milliseconds (Providing Side)",
    )

    @JvmStatic
    val EDC_DATASOURCE_DEFAULT_NAME = ConfigPropRef(
        property = "edc.datasource.default.name",
        defaultDocumentation = "Ensures the EDC initializes the DataSource 'default' because it initializes all edc.datasource.* data sources.",
    )

    @JvmStatic
    val WEB_HTTP_PATH = ConfigPropRef(
        property = "web.http.path",
        defaultDocumentation = "API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the base path.",
    )

    @JvmStatic
    val WEB_HTTP_PORT = ConfigPropRef(
        property = "web.http.port",
        defaultDocumentation = "API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the port.",
    )

    @JvmStatic
    val WEB_HTTP_MANAGEMENT_PATH = ConfigPropRef(
        property = "web.http.management.path",
        defaultDocumentation = "API Group 'Management' contains API endpoints for EDC interaction and " +
            "should be protected from unauthorized access. This is the base path.",
    )

    @JvmStatic
    val WEB_HTTP_MANAGEMENT_PORT = ConfigPropRef(
        property = "web.http.management.port",
        defaultDocumentation = "API Group 'Management' contains API endpoints for EDC interaction and " +
            "should be protected from unauthorized access. This is the port.",
    )

    @JvmStatic
    val WEB_HTTP_PROTOCOL_PATH = ConfigPropRef(
        property = "web.http.protocol.path",
        defaultDocumentation = "API Group 'Protocol' must be public as it is used for connector to connector communication, this is the base path.",
    )

    @JvmStatic
    val WEB_HTTP_PROTOCOL_PORT = ConfigPropRef(
        property = "web.http.protocol.port",
        defaultDocumentation = "API Group 'Protocol' must be public as it is used for connector to connector communication, this is the port.",
    )

    @JvmStatic
    val WEB_HTTP_CONTROL_PATH = ConfigPropRef(
        property = "web.http.control.path",
        defaultDocumentation = "API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the base path.",
    )

    @JvmStatic
    val WEB_HTTP_CONTROL_PORT = ConfigPropRef(
        property = "web.http.control.port",
        defaultDocumentation = "API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the port.",
    )

    @JvmStatic
    val WEB_HTTP_PUBLIC_PATH = ConfigPropRef(
        property = "web.http.public.path",
        defaultDocumentation = "API Group 'Public' contains public data plane API endpoints. This is the base path.",
    )

    @JvmStatic
    val WEB_HTTP_PUBLIC_PORT = ConfigPropRef(
        property = "web.http.public.port",
        defaultDocumentation = "API Group 'Public' contains public data plane API endpoints. This is the port.",
    )

    @JvmStatic
    val EDC_JSONLD_HTTPS_ENABLED = ConfigPropRef(
        property = "edc.jsonld.https.enabled",
        defaultDocumentation = "Required to be set since Eclipse EDC 0.2.1",
    )

    @JvmStatic
    val EDC_JSONLD_HTTP_ENABLED = ConfigPropRef(
        property = "edc.jsonld.http.enabled",
        defaultDocumentation = "Required to be set since Eclipse EDC 0.7.2",
    )

    @JvmStatic
    val EDC_RUNTIME_ID = ConfigPropRef(
        property = "edc.runtime.id",
        defaultDocumentation = "Connector Name for logging purposes",
    )

    @JvmStatic
    val EDC_PARTICIPANT_ID = ConfigPropRef(
        property = "edc.participant.id",
        defaultDocumentation = "Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector.",
    )

    @JvmStatic
    val EDC_HOSTNAME = ConfigPropRef(
        property = "edc.hostname",
        defaultDocumentation = "Fully Qualified Domain Name of where the Connector is hosted, e.g. `my-connector.myorg.com`",
    )

    @JvmStatic
    val EDC_DSP_CALLBACK_ADDRESS = ConfigPropRef(
        property = "edc.dsp.callback.address",
        defaultDocumentation = "Full URL for the DSP callback address",
    )

    @JvmStatic
    val SOVITY_EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD = ConfigPropRef(
        property = "sovity.edc.ui.management.api.url.shown.in.dashboard",
        defaultDocumentation = "URL shown in the EDC UI for the management API. This might differ from the default " +
            "Management API URL if an auth proxy solution has been put between",
    )

    @JvmStatic
    val EDC_DATAPLANE_API_PUBLIC_BASEURL = ConfigPropRef(
        property = "edc.dataplane.api.public.baseurl",
        defaultDocumentation = "Data Plane Public API (V2): Data Plane Public API V2 as reachable by the outside",
    )

    @JvmStatic
    val EDC_TRANSFER_PROXY_TOKEN_SIGNER_PRIVATEKEY_ALIAS = ConfigPropRef(
        property = "edc.transfer.proxy.token.signer.privatekey.alias",
        defaultDocumentation = "Vault Entry: ${CeVaultEntries.TRANSFER_PROXY_PRIVATE.documentation}",
    )

    @JvmStatic
    val EDC_TRANSFER_PROXY_TOKEN_VERIFIER_PUBLICKEY_ALIAS = ConfigPropRef(
        property = "edc.transfer.proxy.token.verifier.publickey.alias",
        defaultDocumentation = "Vault Entry: ${CeVaultEntries.TRANSFER_PROXY_PUBLIC.documentation}",
    )

    @JvmStatic
    val EDC_DATAPLANE_TRANSFERTYPES = ConfigPropRef(
        property = "edc.dataplane.transfertypes",
        defaultDocumentation = "Data Plane Transfer Types, not to be confused with the Data Plane Source and Dest Types",
    )

    @JvmStatic
    val EDC_DATAPLANE_DESTTYPES = ConfigPropRef(
        property = "edc.dataplane.desttypes",
        defaultDocumentation = "Data Plane Destination Types",
    )

    @JvmStatic
    val EDC_DATAPLANE_SOURCETYPES = ConfigPropRef(
        property = "edc.dataplane.sourcetypes",
        defaultDocumentation = "Data Plane Source Types",
    )

    @JvmStatic
    val EDC_DATAPLANE_TOKEN_VALIDATION_ENDPOINT = ConfigPropRef(
        property = "edc.dataplane.token.validation.endpoint",
        defaultDocumentation = "For dataplanes: Endpoint for validating tokens (on the control plane)"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_PROTOCOL = ConfigPropRef(
        property = "sovity.internal.cp.protocol",
        defaultDocumentation = """
            HTTP Protocol to use for the standalone data plane to communicate with the standalone control plane.
            Should be `http://` or `https://`.
            Is used for building the Control Plane's Control API URL and Management API URL in the standalone data plane
        """.trimIndent(),
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_FQDN = ConfigPropRef(
        property = "sovity.internal.cp.fqdn",
        defaultDocumentation = """
            Control plane internal fqdn.
            Hostname of the standalone control plane from the perspective of a standalone data plane.
            Is used for building the Control Plane's Control API URL and Management API URL in
            the standalone data plane
        """.trimIndent(),
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_FIRST_PORT = ConfigPropRef(
        property = "sovity.internal.cp.first.port",
        defaultDocumentation = """
            Set this if the control plane uses a non-default first port.
            Is used for building the Control Plane's Control API URL and Management API URL in the standalone data plane
        """.trimIndent(),
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_BASE_PATH = ConfigPropRef(
        property = "sovity.internal.cp.base.path",
        defaultDocumentation = """
            Set this if the control plane uses a non-default base path.
            Is used for building the Control Plane's Control API URL and Management API URL in the standalone data plane
        """.trimIndent()
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PATH = ConfigPropRef(
        property = "sovity.internal.cp.web.http.control.path",
        defaultDocumentation = "Is used for building the Control Plane's Control API URLs in the standalone data plane"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PORT = ConfigPropRef(
        property = "sovity.internal.cp.web.http.control.port",
        defaultDocumentation = "Is used for building the Control Plane's Control API URLs in the standalone data plane"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PATH = ConfigPropRef(
        property = "sovity.internal.cp.web.http.management.path",
        defaultDocumentation = "Is used for building the Control Plane's Management API URLs in the standalone data plane"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PORT = ConfigPropRef(
        property = "sovity.internal.cp.web.http.management.port",
        defaultDocumentation = "Is used for building the Control Plane's Management API URLs in the standalone data plane"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY = ConfigPropRef(
        property = "sovity.internal.cp.management.api.key",
        defaultDocumentation = "API Key the control plane's Management API. The data plane uses this to register itself"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY_HEADER = ConfigPropRef(
        property = "sovity.internal.cp.management.api.key.header",
        defaultDocumentation = "API Key Header Name for the control plane's Management API. The data plane uses this to register itself"
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_CONTROL_API_URL = ConfigPropRef(
        property = "sovity.internal.cp.control.api.url",
        defaultDocumentation = "Control API URL of the control plane, as reachable by the standalone data plane."
    )

    @JvmStatic
    val SOVITY_INTERNAL_CP_MANAGEMENT_API_URL = ConfigPropRef(
        property = "sovity.internal.cp.management.api.url",
        defaultDocumentation = "Management API URL the control plane, as reachable by the standalone data plane."
    )

    @JvmStatic
    val SOVITY_E2E_TEST_UTILITIES_ENABLED = ConfigPropRef(
        property = "sovity.e2e.test.utilities.enabled",
        defaultDocumentation = "Special endpoints used in some integration or e2e tests.",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_URL = ConfigPropRef(
        property = "edc.vault.hashicorp.url",
        defaultDocumentation = "The URL of the Vault"
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_HEALTH_CHECK_ENABLED = ConfigPropRef(
        property = "edc.vault.hashicorp.health.check.enabled",
        defaultDocumentation = "`boolean` Whether or not the vault health check is enabled."
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_API_HEALTH_CHECK_PATH = ConfigPropRef(
        property = "edc.vault.hashicorp.api.health.check.path",
        defaultDocumentation = "The URL path of the vault's /health endpoint",
    )


    @JvmStatic
    val EDC_VAULT_HASHICORP_HEALTH_CHECK_STANDBY_OK = ConfigPropRef(
        property = "edc.vault.hashicorp.health.check.standby.ok",
        defaultDocumentation = "`boolean` Specifies if being a standby should still return the active status code instead of the standby status code",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_TOKEN = ConfigPropRef(
        property = "edc.vault.hashicorp.token",
        defaultDocumentation = "The token used to access the Hashicorp Vault",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_TOKEN_SCHEDULED_RENEW_ENABLED = ConfigPropRef(
        property = "edc.vault.hashicorp.token.scheduled-renew-enabled",
        defaultDocumentation = "`boolean` Whether the automatic token renewal process will be triggered or not. Should be disabled only for development and testing purposes",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_TOKEN_TTL = ConfigPropRef(
        property = "edc.vault.hashicorp.token.ttl",
        defaultDocumentation = "`long` The time-to-live (ttl) value of the Hashicorp Vault token in seconds",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_TOKEN_RENEW_BUFFER = ConfigPropRef(
        property = "edc.vault.hashicorp.token.renew-buffer",
        defaultDocumentation = "`long` The renew buffer of the Hashicorp Vault token in seconds",
    )

    @JvmStatic
    val EDC_VAULT_HASHICORP_API_SECRET_PATH = ConfigPropRef(
        property = "edc.vault.hashicorp.api.secret.path",
        defaultDocumentation = "The URL path of the vault's /secret endpoint",
    )

    @JvmStatic
    val EDC_DATAPLANE_ENDPOINT_CONTROL_TRANSFER = ConfigPropRef(
        property = "edc.dataplane.endpoint.control.transfer",
        defaultDocumentation = "Endpoint on the Control API of this data plane, as reachable by the control plane, for the control plane to talk back to the data plane with"
    )

    @JvmStatic
    val EDC_IAM_ISSUER_ID = ConfigPropRef(
        property = "edc.iam.issuer.id",
        defaultDocumentation = "DID of this connector. Starts with did:web:"
    )

    @JvmStatic
    val EDC_IAM_STS_DIM_URL = ConfigPropRef(
        property = "edc.iam.sts.dim.url",
        defaultDocumentation = "STS Dim endpoint"
    )

    @JvmStatic
    val EDC_IAM_STS_OAUTH_CLIENT_ID = ConfigPropRef(
        property = "edc.iam.sts.oauth.client.id",
        defaultDocumentation = "STS OAuth2 client id"
    )

    @JvmStatic
    val EDC_IAM_STS_OAUTH_TOKEN_URL = ConfigPropRef(
        property = "edc.iam.sts.oauth.token.url",
        defaultDocumentation = "STS OAuth2 endpoint for requesting a token"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_ALIAS = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.governance.alias",
        defaultDocumentation = "The alias of the scope 'governance'"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_TYPE = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.governance.type",
        defaultDocumentation = "The credential type of the scope 'governance'"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_OPERATION = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.governance.operation",
        defaultDocumentation = "The operation of the scope 'governance' e.g. 'read'"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_ALIAS = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.membership.alias",
        defaultDocumentation = "The alias of the scope 'membership'"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_TYPE = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.membership.type",
        defaultDocumentation = "The credential type of the scope 'membership'"
    )

    @JvmStatic
    val EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_OPERATION = ConfigPropRef(
        property = "edc.iam.iatp.default-scopes.membership.operation",
        defaultDocumentation = "The operation of the scope 'membership' e.g. 'read'"
    )

    @JvmStatic
    val TX_IAM_IATP_BDRS_SERVER_URL = ConfigPropRef(
        property = "tx.iam.iatp.bdrs.server.url",
        defaultDocumentation = "Base URL of the BDRS service"
    )

    @JvmStatic
    val EDC_IAM_TRUSTED_ISSUER_COFINITY_ID = ConfigPropRef(
        property = "edc.iam.trusted-issuer.cofinity.id",
        defaultDocumentation = "DID of the issuer, starts with did:web:"
    )

    @JvmStatic
    val EDC_IAM_STS_OAUTH_CLIENT_SECRET_ALIAS = ConfigPropRef(
        property = "edc.iam.sts.oauth.client.secret.alias",
        defaultDocumentation = "Vault alias for the STS oauth client secret"
    )

    @JvmStatic
    val WEB_HTTP_CONSUMER_API_PATH = ConfigPropRef(
        property = "web.http.consumer.api.path",
        defaultDocumentation = "API Group 'Consumer API' for Tractus-X Data planes. This is the base path",
    )

    @JvmStatic
    val TX_DPF_CONSUMER_PROXY_PORT = ConfigPropRef(
        property = "tx.dpf.consumer.proxy.port",
        defaultDocumentation = "API Group 'Consumer API' for Tractus-X Data planes. This is the port.",
    )

    @JvmStatic
    val EDC_SQL_FETCH_SIZE = ConfigPropRef(
        property = "edc.sql.fetch.size",
        defaultDocumentation = "Fetch size value used in SQL queries"
    )

    @JvmStatic
    val SOVITY_EDC_UI_LOGOUT_URL = ConfigPropRef(
        property = "sovity.edc.ui.logout.url",
        defaultDocumentation = "Logout URL to log out from in the EDC UI"
    )

    @JvmStatic
    val SOVITY_EDC_UI_DOCUMENTATION_URL = ConfigPropRef(
        property = "sovity.edc.ui.documentation.url",
        defaultDocumentation = "Documentation URL used in the EDC UI"
    )

    @JvmStatic
    val SOVITY_EDC_UI_SUPPORT_URL = ConfigPropRef(
        property = "sovity.edc.ui.support.url",
        defaultDocumentation = "Service Desk URL used in the EDC UI"
    )

    @JvmStatic
    val SOVITY_EDC_UI_PRIVACY_POLICY_URL = ConfigPropRef(
        property = "sovity.edc.ui.privacy.policy.url",
        defaultDocumentation = "Privacy Policy URL used in the EDC UI"
    )

    @JvmStatic
    val SOVITY_EDC_UI_LEGAL_NOTICE_URL = ConfigPropRef(
        property = "sovity.edc.ui.legal.notice.url",
        defaultDocumentation = "Legal Notice URL used in the EDC UI"
    )

    @JvmStatic
    val SOVITY_EDC_UI_FEATURES = ConfigPropRef(
        property = "sovity.edc.ui.features",
        defaultDocumentation = "Comma separated enabled EDC UI features. Note that most of these require a sovity EDC EE backend.\n\n" +
            "Available values:\n" +
            UiConfigFeature.entries.joinToString("\n") { " * `$it`" },
    )

    @JvmStatic
    val SOVITY_EDC_UI_FEATURES_ADD = ConfigPropRef(
        property = "sovity.edc.ui.features.add",
        defaultDocumentation = "Comma separated EDC UI features to add on top of `${SOVITY_EDC_UI_FEATURES.property}`. Note that most of these require a sovity EDC EE backend.\n\n" +
            "Available values:\n" +
            UiConfigFeature.entries.joinToString("\n") { " * `$it`" }
    )

    @JvmStatic
    val SOVITY_EDC_UI_FEATURES_EXCLUDE = ConfigPropRef(
        property = "sovity.edc.ui.features.exclude",
        defaultDocumentation = "Comma separated enabled EDC UI to exclude from `${SOVITY_EDC_UI_FEATURES.property}`. Note that most of these require a sovity EDC EE backend.\n\n" +
            "Available values:\n" +
            UiConfigFeature.entries.joinToString("\n") { " * `$it`" }
    )

    @JvmStatic
    val SOVITY_EDC_UI_PRECONFIGURED_COUNTERPARTIES = ConfigPropRef(
        property = "sovity.edc.ui.preconfigured.counterparties",
        defaultDocumentation = "Comma separated list of enabled preconfigured counterparties. format is https://connector/api/dsp?participantId=abc"
    )
}
