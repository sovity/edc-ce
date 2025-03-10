/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination.query;

import de.sovity.edc.ce.modules.messaging.contract_termination.ContractAgreementTerminationDetails;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.jooq.DSLContext;

import static de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_AGREEMENT;
import static de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_NEGOTIATION;
import static de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;

@RequiredArgsConstructor
public class ContractAgreementTerminationDetailsQuery {

    public ContractAgreementTerminationDetails fetchAgreementDetailsOrThrow(DSLContext dsl, String agreementId) {
        val n = EDC_CONTRACT_NEGOTIATION;
        val a = EDC_CONTRACT_AGREEMENT;
        val t = SOVITY_CONTRACT_TERMINATION;

        return dsl.select(
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
            .fetchOneInto(ContractAgreementTerminationDetails.class);
    }
}
