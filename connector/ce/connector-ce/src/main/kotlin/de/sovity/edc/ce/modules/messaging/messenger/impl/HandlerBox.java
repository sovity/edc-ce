/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import org.eclipse.edc.spi.agent.ParticipantAgent;

import java.util.function.BiFunction;

public record HandlerBox<IN, OUT>(
    Class<IN> clazz,
    BiFunction<ParticipantAgent, IN, OUT> handler
) {
}
