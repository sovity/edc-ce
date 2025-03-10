/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination.query;

import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy;
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractTerminationParam;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;

@RequiredArgsConstructor
public class TerminateContractQuery {

    public OffsetDateTime terminateConsumerAgreementOrThrow(
        DSLContext dsl,
        ContractTerminationParam termination,
        ContractTerminatedBy terminatedBy
    ) {
        val tooAccurate = OffsetDateTime.now();
        val now = tooAccurate.truncatedTo(ChronoUnit.MICROS);

        val newTermination = dsl.newRecord(SOVITY_CONTRACT_TERMINATION);
        newTermination.setContractAgreementId(termination.contractAgreementId());
        newTermination.setDetail(termination.detail());
        newTermination.setReason(termination.reason());
        newTermination.setTerminatedBy(terminatedBy);
        newTermination.setTerminatedAt(now);

        newTermination.insert();

        return now;
    }
}
