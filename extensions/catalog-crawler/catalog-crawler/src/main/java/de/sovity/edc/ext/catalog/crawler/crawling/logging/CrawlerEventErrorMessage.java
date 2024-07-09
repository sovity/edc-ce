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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import de.sovity.edc.ext.catalog.crawler.utils.StringUtils2;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Helper Dto that contains User Message + Error Stack Trace to be written into
 * {@link de.sovity.edc.ext.catalog.crawler.db.jooq.tables.CrawlerEventLog}.
 * <br>
 * This class exists so that logging exceptions has a consistent format.
 *
 * @param message message
 * @param stackTraceOrNull stack trace
 */
public record CrawlerEventErrorMessage(String message, String stackTraceOrNull) {

    public static CrawlerEventErrorMessage ofMessage(@NonNull String message) {
        return new CrawlerEventErrorMessage(message, null);
    }

    public static CrawlerEventErrorMessage ofStackTrace(@NonNull String baseMessage, @NonNull Throwable cause) {
        var message = baseMessage;
        message = StringUtils2.removeSuffix(message, ".");
        message = StringUtils2.removeSuffix(message, ":");
        message = "%s: %s".formatted(message, cause.getClass().getName());
        return new CrawlerEventErrorMessage(message, ExceptionUtils.getStackTrace(cause));
    }
}
