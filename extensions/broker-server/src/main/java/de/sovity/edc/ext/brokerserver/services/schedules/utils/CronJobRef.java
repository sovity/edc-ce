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

package de.sovity.edc.ext.brokerserver.services.schedules.utils;

import org.quartz.Job;

import java.util.function.Supplier;

/**
 * Broker Server CRON Job.
 *
 * @param configPropertyName EDC Config property that decides cron expression
 * @param clazz              class of the job
 * @param factory            factory that initializes the task class
 * @param <T>                job type
 */
public record CronJobRef<T extends Job>(
        String configPropertyName,
        Class<T> clazz,
        Supplier<T> factory
) {

    @SuppressWarnings("unchecked")
    public Supplier<Job> asJobSupplier() {
        return (Supplier<Job>) factory;
    }
}
