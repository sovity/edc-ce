/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeEnvironment
import de.sovity.edc.ce.config.getCeEnvironment
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.control_api.ControlApiUrlOverrideModule
import de.sovity.edc.ce.modules.test_utils.TestBackendModule
import de.sovity.edc.ce.modules.vault.hashicorp.HashicorpVaultModule
import de.sovity.edc.ce.modules.vault.inmemory.InMemoryVaultModule
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.RuntimeModule
import de.sovity.edc.runtime.modules.getEnvironment
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.sql.SqlQueryExecutorConfiguration

@Suppress("MaxLineLength")
object CeRootModule {

    @JvmStatic
    fun ceRoot() = EdcModule(
        name = "root-ce",
        documentation = "sovity EDC CE Root module, contains super-global Extensions, kick-starts our module system."
    ).apply {
        dependencyBundle(CeDependencyBundles.root)

        module(baseConnector())

        moduleOneOf(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_DEPLOYMENT_KIND,
            { required() },
            CeControlPlaneModules.withIntegratedDataPlane(),
            CeControlPlaneModules.standalone(),
            CeDataPlaneModules.standalone(),
            TestBackendModule.instance(),
        )

        moduleOneOf(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_VAULT_KIND,
            { defaultValue(InMemoryVaultModule.instance().name) },
            InMemoryVaultModule.instance(),
            HashicorpVaultModule.instance(),
        )
    }

    fun baseConnector() = EdcModule(
        name = "root-base-config",
        documentation = "Base configuration and overrides for both control planes and data planes"
    ).apply {
        modules(RuntimeModule.instance())
        property(
            ConfigPropCategory.OVERRIDES,
            RuntimeConfigProps.SOVITY_ENVIRONMENT
        ) {
            enumValues(CeEnvironment::class.java)
            required()
            warnIfOverridden = true
            defaultValue(CeEnvironment.PRODUCTION.name)
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_FQDN_PUBLIC
        ) {
            requiredInProd()
            defaultValueOutsideProd("localhost") // should not be applied if required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_FQDN_INTERNAL
        ) {
            defaultValue("localhost")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_BASE_PATH,
        ) {
            defaultValue("/")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_BUILD_DATE
        )
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_BUILD_VERSION
        )
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_SQL_FETCH_SIZE
        ) {
            defaultValue(SqlQueryExecutorConfiguration.DEFAULT_EDC_SQL_FETCH_SIZE)
        }
        configureWebApi()
        configureControlApi()
        configureCommonOverrides()

        moduleIfTrue(
            ConfigPropCategory.OPTIONAL,
            TestBackendModule.instance(),
            CeConfigProps.SOVITY_E2E_TEST_UTILITIES_ENABLED
        ) {
            // TODO refactor legacy tests to not depend on this being true by default
            defaultValue("true")
        }
    }

    private fun EdcModule.configureWebApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_DEFAULT_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_DEFAULT_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 1)
            warnIfOverridden = true
        }
    }

    private fun EdcModule.configureControlApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_CONTROL_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api/control`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/control")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_CONTROL_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 4)
            warnIfOverridden = true
        }
        module(ControlApiUrlOverrideModule.instance())
    }

    private fun EdcModule.configureCommonOverrides() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_JSONLD_HTTPS_ENABLED
        ) {
            defaultValue("true")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_JSONLD_HTTP_ENABLED
        ) {
            defaultValue("true")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_HOSTNAME
        ) {
            defaultFromProp(CeConfigProps.SOVITY_EDC_FQDN_PUBLIC)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_HTTP_PROTOCOL
        ) {
            defaultValueFn = DocumentedFn("Defaults to `https://` in prod") { config ->
                when (config.getCeEnvironment()) {
                    CeEnvironment.PRODUCTION -> "https://"
                    CeEnvironment.UNIT_TEST,
                    CeEnvironment.LOCAL_DEMO_DOCKER_COMPOSE -> "http://"

                    null -> error("Invalid Environment: ${config.getEnvironment()}")
                }
            }
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_HTTP_CLIENT_HTTPS_ENFORCE
        ) {
            defaultValueFn = DocumentedFn(
                "Defaults to `false`. Because JSON-LD documents are on `http://` domains often. Especially set to `false` if `${CeConfigProps.SOVITY_HTTP_PROTOCOL.property}`=`http://`"
            ) {
                "false"
            }
        }
    }
}
