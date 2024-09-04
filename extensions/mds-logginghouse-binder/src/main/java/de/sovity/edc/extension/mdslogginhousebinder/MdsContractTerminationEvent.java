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

package de.sovity.edc.extension.mdslogginhousebinder;

import com.truzzt.extension.logginghouse.client.events.CustomLoggingHouseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MdsContractTerminationEvent extends CustomLoggingHouseEvent {
    private final String eventId;
    private final String processId;
    private final String messageBody;
}
