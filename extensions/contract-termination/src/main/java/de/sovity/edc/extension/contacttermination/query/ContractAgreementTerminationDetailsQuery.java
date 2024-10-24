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

import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationDetails;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.jooq.DSLContext;

import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_AGREEMENT;
import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_NEGOTIATION;
import static de.sovity.edc.ext.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;

@RequiredArgsConstructor
public class ContractAgreementTerminationDetailsQuery {

    public ContractAgreementTerminationDetails fetchAgreementDetailsOrThrow(DSLContext dsl, String agreementId) {
        val n = EDC_CONTRACT_NEGOTIATION;
        val a = EDC_CONTRACT_AGREEMENT;
        val t = SOVITY_CONTRACT_TERMINATION;

        return dsl.transactionResult(trx ->
            trx.dsl().select(
                    n.AGREEMENT_ID,
                    n.COUNTERPARTY_ID,
                    n.COUNTERPARTY_ADDRESS,
                    n.TYPE.convertFrom(ContractNegotiation.Type::valueOf),
                    a.PROVIDER_AGENT_ID,
                    a.CONSUMER_AGENT_ID,
                    t.REASON,
                    t.DETAIL,
                    t.TERMINATED_AT,
                    t.TERMINATED_BY)
                .from(
                    n.join(a).on(n.AGREEMENT_ID.eq(a.AGR_ID))
                        .leftJoin(SOVITY_CONTRACT_TERMINATION).on(n.AGREEMENT_ID.eq(t.CONTRACT_AGREEMENT_ID)))
                .where(a.AGR_ID.eq(agreementId))
                .fetchOneInto(ContractAgreementTerminationDetails.class));
    }
}
