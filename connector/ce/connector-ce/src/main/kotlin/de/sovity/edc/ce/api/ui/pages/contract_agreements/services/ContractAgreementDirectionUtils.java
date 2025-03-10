/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ce.api.ui.model.ContractAgreementDirection;
import lombok.experimental.UtilityClass;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;

@UtilityClass
public class ContractAgreementDirectionUtils {

    public static ContractAgreementDirection fromType(ContractNegotiation.Type type) {
        return switch (type) {
            case PROVIDER -> ContractAgreementDirection.PROVIDING;
            case CONSUMER -> ContractAgreementDirection.CONSUMING;
        };
    }
}
