/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import org.eclipse.edc.policy.context.request.spi.RequestPolicyContext;
import org.eclipse.edc.spi.iam.RequestContext;
import org.eclipse.edc.spi.iam.RequestScope;

public class SovityMessagePolicyProvider implements RequestPolicyContext.Provider {
    @Override
    public RequestPolicyContext instantiate(RequestContext requestContext, RequestScope.Builder requestScopeBuilder) {
        return new SovityMessagePolicyContext(requestContext, requestScopeBuilder);
    }
}
