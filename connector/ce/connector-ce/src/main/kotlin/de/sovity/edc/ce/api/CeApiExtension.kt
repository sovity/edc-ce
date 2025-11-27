/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import de.sovity.edc.ce.api.control.OnDemandAssetDataSourceController
import de.sovity.edc.ce.api.ui.UiResourceImpl
import de.sovity.edc.ce.api.ui.pages.dashboard.services.OwnConnectorEndpointServiceImpl
import de.sovity.edc.ce.api.usecase.UseCaseResourceImpl
import de.sovity.edc.ce.libs.mappers.policy.ExpressionExtractor
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrApiService
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractAgreementTerminationService
import de.sovity.edc.ce.modules.policy_utils.always_true.AlwaysTruePolicyDefinitionService
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.simple_di.SimpleDi
import de.sovity.edc.runtime.simple_di.SimpleDiExt.onInstanceCreatedRegisterEdcService
import org.eclipse.edc.azure.blob.api.BlobStoreApi
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore
import org.eclipse.edc.connector.controlplane.contract.spi.offer.store.ContractDefinitionStore
import org.eclipse.edc.connector.controlplane.policy.spi.store.PolicyDefinitionStore
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService
import org.eclipse.edc.connector.controlplane.services.spi.contractnegotiation.ContractNegotiationService
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore
import org.eclipse.edc.edr.spi.store.EndpointDataReferenceStore
import org.eclipse.edc.jsonld.spi.JsonLd
import org.eclipse.edc.policy.engine.spi.PolicyEngine
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.constants.CoreConstants
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.types.TypeManager
import org.eclipse.edc.transform.spi.TypeTransformerRegistry
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry
import org.eclipse.edc.web.spi.WebService
import org.eclipse.edc.web.spi.configuration.ApiContext

/**
 * EDC extension which provides API implementations for most of the Wrapper API endpoints.
 * Excluded endpoints are either implemented in other sovity Community Edition EDC Extensions
 * or implemented in our Enterprise Edition.
 *
 *
 * More information available at:
 *  - docs/product/api_wrapper.md
 */
class CeApiExtension : ServiceExtension {
    @Inject
    private lateinit var alwaysTruePolicyDefinitionService: AlwaysTruePolicyDefinitionService

    @Inject
    private lateinit var catalogService: CatalogService

    @Inject
    private lateinit var configUtils: ConfigUtils

    @Inject
    private lateinit var contractAgreementService: ContractAgreementService

    @Inject
    private lateinit var contractAgreementTerminationService: ContractAgreementTerminationService

    @Inject
    private lateinit var contractDefinitionService: ContractDefinitionService

    @Inject
    private lateinit var contractDefinitionStore: ContractDefinitionStore

    @Inject
    private lateinit var contractNegotiationService: ContractNegotiationService

    @Inject
    private lateinit var contractNegotiationStore: ContractNegotiationStore

    @Inject
    private lateinit var dslContextFactory: DslContextFactory

    @Inject
    private lateinit var edrApiService: EdrApiService

    @Inject
    private lateinit var endpointDataReferenceStore: EndpointDataReferenceStore

    @Inject
    private lateinit var jsonLd: JsonLd

    @Inject
    private lateinit var jsonObjectValidatorRegistry: JsonObjectValidatorRegistry

    @Inject
    private lateinit var policyDefinitionService: PolicyDefinitionService

    @Inject
    private lateinit var policyDefinitionStore: PolicyDefinitionStore

    @Inject
    private lateinit var policyEngine: PolicyEngine

    @Inject
    private lateinit var ruleBindingRegistry: RuleBindingRegistry

    @Inject
    private lateinit var transferProcessService: TransferProcessService

    @Inject
    private lateinit var transferProcessStore: TransferProcessStore

    @Inject
    private lateinit var typeManager: TypeManager

    @Inject
    private lateinit var typeTransformerRegistry: TypeTransformerRegistry

    @Inject
    private lateinit var webService: WebService

    @Inject
    private lateinit var vault: Vault

    @Inject
    private lateinit var blobStoreApi: BlobStoreApi

    override fun initialize(context: ServiceExtensionContext) {
        val objectMapper = typeManager.getMapper(CoreConstants.JSON_LD)
        fixObjectMapperDateSerialization(objectMapper)

        val instances = SimpleDi()
            .addAllowedPackage(
                CeApiExtension::class.java.packageName,
                "de.sovity.edc.ce.libs.mappers",
            )
            .addClassesToInstantiate(
                // the order is important here
                ExpressionExtractor::class.java,
                OwnConnectorEndpointServiceImpl::class.java,
                UiResourceImpl::class.java,
                UseCaseResourceImpl::class.java,
                OnDemandAssetDataSourceController::class.java,
            )
            .addInstances(
                context.config,
                context.monitor,
                alwaysTruePolicyDefinitionService,
                catalogService,
                configUtils,
                contractAgreementTerminationService,
                contractDefinitionService,
                contractDefinitionStore,
                contractNegotiationService,
                contractNegotiationStore,
                dslContextFactory,
                edrApiService,
                endpointDataReferenceStore,
                jsonLd,
                jsonObjectValidatorRegistry,
                policyDefinitionService,
                policyDefinitionStore,
                policyEngine,
                ruleBindingRegistry,
                transferProcessService,
                transferProcessStore,
                typeTransformerRegistry,
                objectMapper,
                vault,
                blobStoreApi
            )
            .addInstance(contractAgreementService)
            .onInstanceCreatedRegisterEdcService(context)
            .toInstances()

        webService.registerResource(ApiContext.MANAGEMENT, instances.getSingle(UiResourceImpl::class.java))
        webService.registerResource(ApiContext.MANAGEMENT, instances.getSingle(UseCaseResourceImpl::class.java))

        webService.registerResource(
            ApiContext.PROTOCOL, instances.getSingle(
                OnDemandAssetDataSourceController::class.java
            )
        )
    }

    private fun fixObjectMapperDateSerialization(objectMapper: ObjectMapper) {
        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}
