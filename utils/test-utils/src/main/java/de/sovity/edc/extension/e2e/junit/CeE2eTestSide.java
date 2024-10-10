/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.junit;

import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public enum CeE2eTestSide {
    CONSUMER,
    PROVIDER;

    public String getParticipantId() {
        return name().toLowerCase();
    }


    @Nullable
    public static CeE2eTestSide fromParameterContextOrNull(ParameterContext parameterContext) {
        val isProvider = parameterContext.getParameter().getDeclaredAnnotation(Provider.class) != null;
        val isConsumer = parameterContext.getParameter().getDeclaredAnnotation(Consumer.class) != null;

        if (isProvider && isConsumer) {
            throw new ParameterResolutionException("Either @Provider or @Consumer may be used.");
        }
        if (isConsumer) {
            return CeE2eTestSide.CONSUMER;
        }
        if (isProvider) {
            return CeE2eTestSide.PROVIDER;
        }
        return null;
    }
}
