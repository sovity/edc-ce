/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@Schema(description = "Whether the contract agreement is incoming or outgoing")
public enum ContractAgreementDirection {
    CONSUMING(ContractNegotiation.Type.CONSUMER),
    PROVIDING(ContractNegotiation.Type.PROVIDER);

    private final ContractNegotiation.Type type;

    public static ContractAgreementDirection fromType(ContractNegotiation.Type type) {
        return Arrays.stream(values()).filter(it -> it.type == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No ContractAgreementType for type %s.".formatted(type)
                ));
    }
}
