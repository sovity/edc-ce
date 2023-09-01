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

import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class PolicyDtoBuilder {

    @SneakyThrows
    public PolicyDto buildPolicyFromJson(@NonNull String policyJson) {
        var policyDto = new PolicyDto();
        policyDto.setLegacyPolicy(policyJson);
        policyDto.setPermission(extractPermissions(policyJson));
        return policyDto;
    }

    @NotNull
    private PermissionDto extractPermissions(String policyJson) {
        // TODO
        return new PermissionDto(new ExpressionDto(ExpressionDto.Type.AND, null, List.of(), null, null));
    }
}
