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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.client.gen.JSON;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType;
import de.sovity.edc.utils.JsonUtils;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.List;

public class TestPolicy {
    private static OffsetDateTime today = OffsetDateTime.now();

    public static UiPolicyConstraint createAfterYesterdayConstraint() {
        return UiPolicyConstraint.builder()
            .left("POLICY_EVALUATION_TIME")
            .operator(OperatorDto.GT)
            .right(UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value(today.minusDays(1).toString())
                .build())
            .build();
    }

    public static de.sovity.edc.client.gen.model.UiPolicyCreateRequest createAfterYesterdayPolicyEdcGen() {
        return jsonCast(createAfterYesterdayPolicy(), de.sovity.edc.client.gen.model.UiPolicyCreateRequest.class);
    }

    private static <T, R> R jsonCast(T obj, Class<R> clazz) {
        return JSON.deserialize(JSON.serialize(obj), clazz);
    }

    public static UiPolicyCreateRequest createAfterYesterdayPolicy() {
        return UiPolicyCreateRequest.builder()
            .constraints(List.of(createAfterYesterdayConstraint()))
            .build();
    }

    public static JSONB createAfterYesterdayPolicyJson() {
        var createRequest = TestPolicy.createAfterYesterdayPolicy();
        return getPolicyJsonLd(createRequest);
    }

    /**
     * This method only works in integration tests, because it depends on the broker server extension context.
     */
    public static JSONB getPolicyJsonLd(UiPolicyCreateRequest createRequest) {
        var policyMapper = BrokerServerExtensionContext.instance.policyMapper();
        var jsonLd = policyMapper.buildPolicyJsonLd(policyMapper.buildPolicy(createRequest));
        return JSONB.jsonb(JsonUtils.toJson(jsonLd));
    }
}
