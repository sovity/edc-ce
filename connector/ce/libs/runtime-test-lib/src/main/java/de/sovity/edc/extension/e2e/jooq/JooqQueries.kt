/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.jooq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.db.jooq.tables.records.SovityVaultSecretRecord
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.JSON.jsonOrNull
import java.time.OffsetDateTime

class JooqQueries(
    private val objectMapper: ObjectMapper
) {
    private fun toJsonString(any: Any?): String? =
        any?.let { objectMapper.writeValueAsString(any) }

    private fun toJson(any: Any?): JSON? =
        jsonOrNull(any?.let { objectMapper.writeValueAsString(any) })

    private inline fun <reified T> toJson(elements: List<T>?): JSON? =
        jsonOrNull(
            elements.let {
                objectMapper.writerFor(object : TypeReference<List<T>>() {}).writeValueAsString(elements)
            }
        )

    fun insertAsset(dsl: DSLContext, asset: Asset) {
        val record = dsl.newRecord(Tables.EDC_ASSET).also {
            it.assetId = asset.id
            it.createdAt = asset.createdAt
            it.properties = toJson(asset.properties)
            it.privateProperties = toJson(asset.privateProperties)
            it.dataAddress = toJson(asset.dataAddress.properties)
        }
        record.insert()
    }

    fun insertAssets(dsl: DSLContext, assets: List<Asset>) {
        val records = assets.map { asset ->
            dsl.newRecord(Tables.EDC_ASSET).also {
                it.assetId = asset.id
                it.createdAt = asset.createdAt
                it.properties = toJson(asset.properties)
                it.privateProperties = toJson(asset.privateProperties)
                it.dataAddress = toJson(asset.dataAddress.properties)
            }
        }
        dsl.batchInsert(records).execute()
    }

    fun removeAllAssets(dsl: DSLContext) = dsl.deleteFrom(Tables.EDC_ASSET).execute()

    fun removeAllVaultSecrets(dsl: DSLContext) = dsl.deleteFrom(Tables.SOVITY_VAULT_SECRET).execute()

    fun insertVaultSecret(dsl: DSLContext, key: String, addMore: (SovityVaultSecretRecord) -> Unit = {}) {
        dsl.newRecord(Tables.SOVITY_VAULT_SECRET).also {
            it.key = key
            it.description = "$key description"
            it.updatedAt = OffsetDateTime.now()
            addMore(it)
            it.insert()
        }
    }

    fun insertEdcBusinessPartnerGroup(dsl: DSLContext, bpn: String, groups: List<String> = emptyList()) {
        dsl.newRecord(Tables.EDC_BUSINESS_PARTNER_GROUP).also {
            it.bpn = bpn
            it.groups = toJson(groups)
            it.insert()
        }
    }

    fun removeAllEdcBusinessPartners(dsl: DSLContext) = dsl.deleteFrom(Tables.EDC_BUSINESS_PARTNER_GROUP).execute()

    fun insertPolicies(dsl: DSLContext, policyDefinitions: List<PolicyDefinition>) {
        val records = policyDefinitions.map { policyDef ->
            dsl.newRecord(Tables.EDC_POLICYDEFINITIONS).also {
                val policy = policyDef.policy

                it.policyId = policyDef.id
                it.createdAt = policyDef.createdAt
                it.privateProperties = toJson(policyDef.privateProperties)

                it.permissions = toJson(policy.permissions)
                it.prohibitions = toJson(policy.prohibitions)
                it.duties = toJson(policy.obligations)
                it.extensibleProperties = toJson(policy.extensibleProperties)
                it.inheritsFrom = policy.inheritsFrom
                it.assigner = policy.assigner
                it.assignee = policy.assignee
                it.target = policy.target
                it.policyType = toJsonString(policy.type)
                it.profiles = toJson(policy.profiles)
            }
        }
        dsl.batchInsert(records).execute()
    }

    fun insertContractDefinitions(
        dsl: DSLContext,
        contractDefinitions: List<ContractDefinition>
    ) {
        val records = contractDefinitions.map { contractDefinition ->
            dsl.newRecord(Tables.EDC_CONTRACT_DEFINITIONS).also {
                it.contractDefinitionId = contractDefinition.id
                it.accessPolicyId = contractDefinition.accessPolicyId
                it.contractPolicyId = contractDefinition.contractPolicyId
                it.assetsSelector = this.toJson(contractDefinition.assetsSelector)
                it.createdAt = contractDefinition.createdAt
                it.privateProperties = toJson(contractDefinition.privateProperties)
            }
        }

        dsl.batchInsert(records).execute()
    }

    fun insertContractAgreements(
        dsl: DSLContext,
        agreements: List<ContractAgreement>
    ) {
        val records = agreements.map { agreement ->
            dsl.newRecord(Tables.EDC_CONTRACT_AGREEMENT).also {
                it.agrId = agreement.id
                it.providerAgentId = agreement.providerId
                it.consumerAgentId = agreement.consumerId
                it.signingDate = agreement.contractSigningDate
                it.assetId = agreement.assetId
                it.policy = toJson(agreement.policy)
            }
        }

        dsl.batchInsert(records).execute()
    }

    fun insertContractNegotiation(dsl: DSLContext, negotiations: List<ContractNegotiation>) {
        val records = negotiations.map { negotiation ->
            dsl.newRecord(Tables.EDC_CONTRACT_NEGOTIATION).also {
                it.id = negotiation.id
                it.createdAt = negotiation.createdAt
                it.updatedAt = negotiation.updatedAt
                it.correlationId = negotiation.correlationId
                it.counterpartyId = negotiation.counterPartyId
                it.counterpartyAddress = negotiation.counterPartyAddress
                it.protocol = negotiation.protocol
                it.type = negotiation.type.name
                it.state = negotiation.state
                it.stateCount = negotiation.stateCount
                it.stateTimestamp = negotiation.stateTimestamp
                it.errorDetail = negotiation.errorDetail
                it.agreementId = negotiation.contractAgreement?.id
                it.contractOffers = toJson(negotiation.contractOffers)
                it.callbackAddresses = toJson(negotiation.callbackAddresses)
                it.traceContext = toJson(negotiation.traceContext)
                it.pending = negotiation.isPending
                it.protocolMessages = toJson(negotiation.protocolMessages)
                it.leaseId = null
            }
        }

        dsl.batchInsert(records).execute()
    }

    fun insertTransferProcess(dsl: DSLContext, transferProcess: TransferProcess) {
        val record = dsl.newRecord(Tables.EDC_TRANSFER_PROCESS).also {
            it.createdAt = transferProcess.createdAt
            it.correlationId = transferProcess.correlationId
            it.privateProperties = toJson(transferProcess.privateProperties)
            it.state = transferProcess.state
            it.transferprocessId = transferProcess.id
            it.transferType = transferProcess.transferType
            it.type = transferProcess.type.name
            it.updatedAt = transferProcess.updatedAt
        }

        dsl.batchInsert(record).execute()
    }
}
