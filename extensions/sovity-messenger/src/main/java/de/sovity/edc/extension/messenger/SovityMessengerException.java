/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.messenger;

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
