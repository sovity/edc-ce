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

import lombok.NonNull;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JobFactoryImpl implements JobFactory {
    private final Map<Class<?>, Supplier<Job>> factories;

    public JobFactoryImpl(@NonNull Collection<CronJobRef<?>> jobs) {
        factories = jobs.stream().collect(Collectors.toMap(
                CronJobRef::clazz,
                CronJobRef::asJobSupplier
        ));
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        Class<?> jobClazz = bundle.getJobDetail().getJobClass();
        Supplier<Job> factory = factories.get(jobClazz);
        if (factory == null) {
            throw new IllegalArgumentException("No factory for Job class %s. Supported Job classes are: %s.".formatted(
                    jobClazz.getName(),
                    factories.keySet().stream().map(Class::getName).collect(Collectors.joining(", "))
            ));
        }
        return factory.get();
    }
}
