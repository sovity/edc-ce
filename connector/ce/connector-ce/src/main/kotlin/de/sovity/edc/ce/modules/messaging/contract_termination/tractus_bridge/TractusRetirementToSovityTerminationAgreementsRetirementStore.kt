/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge

import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.TractusContractRetirementToSovityContractTerminationQuerySpec.FIELDS
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.limitAndOffset
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.orderBy
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.where
import org.eclipse.edc.spi.query.QuerySpec
import org.eclipse.edc.spi.result.StoreResult
import org.eclipse.tractusx.edc.agreements.retirement.spi.store.AgreementsRetirementStore
import org.eclipse.tractusx.edc.agreements.retirement.spi.types.AgreementsRetirementEntry
import org.jooq.DSLContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.stream.Stream
import de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION as CONTRACT_TERMINATION

/**
 * This is a replacement of the Catena/Tractus AgreementsRetirementStore,
 * see tractus-retirement-to-sovity-termination-bridge.md for an overview.
 */
class TractusRetirementToSovityTerminationAgreementsRetirementStore(
    val dslContextFactory: DslContextFactory,
) : AgreementsRetirementStore {

    companion object {
        const val CATENA_CONTRACT_RETIREMENT_REASON = "Catena Contract Retirement"
    }

    override fun save(entry: AgreementsRetirementEntry): StoreResult<Void> {
        return dslContextFactory.transactionResult { dsl ->
            if (isRetired(dsl, entry.agreementId)) {
                return@transactionResult StoreResult.alreadyExists<Void>(
                    AgreementsRetirementStore.ALREADY_EXISTS_TEMPLATE.format(entry.agreementId)
                )
            }
            insert(dsl, entry)
            StoreResult.success()
        }
    }

    private fun insert(dsl: DSLContext, entry: AgreementsRetirementEntry) {
        val record = CONTRACT_TERMINATION.newRecord().apply {
            contractAgreementId = entry.agreementId
            reason = CATENA_CONTRACT_RETIREMENT_REASON
            detail = entry.reason
            terminatedAt = OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(entry.agreementRetirementDate),
                ZoneOffset.UTC
            )
            terminatedBy = ContractTerminatedBy.SELF
        }

        dsl.batchInsert(record).execute()
    }

    private fun isRetired(dsl: DSLContext, agreementId: String): Boolean =
        dsl.fetchExists(
            dsl.select()
                .from(CONTRACT_TERMINATION)
                .where(CONTRACT_TERMINATION.CONTRACT_AGREEMENT_ID.eq(agreementId))
        )

    override fun delete(contractAgreementId: String): StoreResult<Void> {
        return dslContextFactory.transactionResult { dsl ->
            if (!isRetired(dsl, contractAgreementId)) {
                return@transactionResult StoreResult.notFound<Void>(
                    AgreementsRetirementStore.NOT_FOUND_IN_RETIREMENT_TEMPLATE.format(contractAgreementId)
                )
            }

            val reason = dsl.select(CONTRACT_TERMINATION.REASON)
                .from(CONTRACT_TERMINATION)
                .where(CONTRACT_TERMINATION.CONTRACT_AGREEMENT_ID.eq(contractAgreementId))
                .fetchSingleInto(String::class.java)
            if (reason != CATENA_CONTRACT_RETIREMENT_REASON) {
                return@transactionResult StoreResult.generalError<Void>(
                    "Refusing to re-enable the contract with id=${contractAgreementId} that was not retired via the Catena API."
                )
            }

            dsl.deleteFrom(CONTRACT_TERMINATION)
                .where(CONTRACT_TERMINATION.CONTRACT_AGREEMENT_ID.eq(contractAgreementId))
                .execute()
            StoreResult.success()
        }
    }

    override fun findRetiredAgreements(querySpec: QuerySpec): Stream<AgreementsRetirementEntry> {
        return dslContextFactory.transactionResult { dsl ->
            dsl.selectFrom(CONTRACT_TERMINATION)
                .where(querySpec, FIELDS)
                .orderBy(querySpec, FIELDS)
                .limitAndOffset(querySpec)
                .fetchStream()
                .map {
                    AgreementsRetirementEntry.Builder.newInstance()
                        .withAgreementId(it.contractAgreementId)
                        .withReason(it.detail)
                        .withAgreementRetirementDate(it.terminatedAt.toInstant().toEpochMilli())
                        .build()
                }
        }
    }
}
