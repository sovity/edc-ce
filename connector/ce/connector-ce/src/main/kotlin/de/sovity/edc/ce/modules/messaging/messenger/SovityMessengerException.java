/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import lombok.Getter;

public class SovityMessengerException extends RuntimeException {

    @Getter
    private String body;
    private Object payload;

    public SovityMessengerException(String message) {
        super(message);
    }

    public SovityMessengerException(String message, String body, Object payload) {
        super(message);
        this.body = body;
    }
}
