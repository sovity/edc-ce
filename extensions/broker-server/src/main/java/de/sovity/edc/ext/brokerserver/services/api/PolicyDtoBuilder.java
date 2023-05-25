/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.services.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.Policy;

@RequiredArgsConstructor
public class PolicyDtoBuilder {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public PolicyDto buildPolicyFromJson(@NonNull String policyJson) {
        var policy = objectMapper.readValue(policyJson, Policy.class);
        return new PolicyDto(policy);
    }
}
