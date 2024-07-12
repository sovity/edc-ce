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
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jooq.DSLContext;

import java.util.Optional;
import java.util.function.Supplier;

import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_AGREEMENT;
import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_NEGOTIATION;
import static de.sovity.edc.ext.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;

@RequiredArgsConstructor
public class ContractAgreementTerminationDetailsQuery {
    private final Supplier<DSLContext> dsl;

    public Optional<ContractAgreementTerminationDetails> fetchAgreementDetails(String agreementId) {
        val n = EDC_CONTRACT_NEGOTIATION;
        val a = EDC_CONTRACT_AGREEMENT;
        val t = SOVITY_CONTRACT_TERMINATION;

        val fetched = dsl.get().select(
                n.AGREEMENT_ID,
                n.COUNTERPARTY_ID,
                n.COUNTERPARTY_ADDRESS,
                n.STATE.convertFrom(ContractNegotiationStates::from),
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
            .where(EDC_CONTRACT_AGREEMENT.AGR_ID.eq(agreementId))
            .fetchOne();

        return Optional.ofNullable(fetched).map(it -> it.into(ContractAgreementTerminationDetails.class));
    }
}
