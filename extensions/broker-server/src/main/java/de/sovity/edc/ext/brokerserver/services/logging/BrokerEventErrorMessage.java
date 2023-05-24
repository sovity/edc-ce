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

package de.sovity.edc.ext.brokerserver.services.logging;

import de.sovity.edc.ext.brokerserver.utils.StringUtils2;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Helper Dto that contains User Message + Error Stack Trace to be written into
 * {@link de.sovity.edc.ext.brokerserver.db.jooq.tables.BrokerEventLog}.
 * <br>
 * This class exists so that logging exceptions has a consistent format.
 *
 * @param message          message
 * @param stackTraceOrNull stack trace
 */
public record BrokerEventErrorMessage(String message, String stackTraceOrNull) {

    public static BrokerEventErrorMessage ofMessage(@NonNull String message) {
        return new BrokerEventErrorMessage(message, null);
    }

    public static BrokerEventErrorMessage ofStackTrace(@NonNull String baseMessage, @NonNull Throwable cause) {
        String message = baseMessage;
        message = StringUtils2.removeSuffix(message, ".");
        message = StringUtils2.removeSuffix(message, ":");
        message = "%s: %s".formatted(message, cause.getClass().getName());
        return new BrokerEventErrorMessage(message, ExceptionUtils.getStackTrace(cause));
    }
}
