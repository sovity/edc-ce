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
 */

package de.sovity.edc.extension.contactcancellation.query;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jooq.DSLContext;

import java.util.Optional;

import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_AGREEMENT;
import static de.sovity.edc.ext.db.jooq.Tables.EDC_CONTRACT_NEGOTIATION;

@RequiredArgsConstructor
public class AgreementDetailsQuery {
    private final DSLContext dsl;

    public Optional<AgreementDetails> fetchAgreementDetails(String agreementId) {
        val n = EDC_CONTRACT_NEGOTIATION;
        val a = EDC_CONTRACT_AGREEMENT;

        val fetched = dsl.select(
                n.COUNTERPARTY_ID,
                n.COUNTERPARTY_ADDRESS,
                n.STATE.convertFrom(ContractNegotiationStates::from),
                a.PROVIDER_AGENT_ID,
                a.CONSUMER_AGENT_ID,
                n.TYPE.convertFrom(ContractNegotiation.Type::valueOf))
            .from(n.join(a).on(n.AGREEMENT_ID.eq(a.AGR_ID)))
            .where(EDC_CONTRACT_AGREEMENT.AGR_ID.eq(agreementId))
            .fetchOne();

        return Optional.ofNullable(fetched).map(it -> it.into(AgreementDetails.class));
    }
}
