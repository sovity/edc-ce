/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.contacttermination.query;

import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import de.sovity.edc.extension.contacttermination.ContractTerminationParam;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static de.sovity.edc.ext.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;

@RequiredArgsConstructor
public class TerminateContractQuery {

    public OffsetDateTime terminateConsumerAgreementOrThrow(
        DSLContext dsl,
        ContractTerminationParam termination,
        ContractTerminatedBy terminatedBy) {

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
