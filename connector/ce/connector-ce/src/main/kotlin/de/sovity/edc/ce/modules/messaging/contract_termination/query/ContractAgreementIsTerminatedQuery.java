/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination.query;

import de.sovity.edc.ce.db.jooq.Tables;
import lombok.val;
import org.jooq.DSLContext;

public class ContractAgreementIsTerminatedQuery {

    public boolean isTerminated(DSLContext dsl, String contractAgreementId) {

        val t = Tables.SOVITY_CONTRACT_TERMINATION;

        return dsl.fetchExists(
            dsl.select()
                .from(t)
                .where(t.CONTRACT_AGREEMENT_ID.eq(contractAgreementId))
        );
    }
}
