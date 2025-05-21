/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import org.eclipse.edc.spi.types.domain.message.ErrorMessage;

public class SovityMessengerError extends ErrorMessage {
    public static final class Builder extends ErrorMessage.Builder<SovityMessengerError, SovityMessengerError.Builder> {
        private Builder() {
            super(new SovityMessengerError());
        }

        public static SovityMessengerError.Builder newInstance() {
            return new SovityMessengerError.Builder();
        }

        @Override
        protected SovityMessengerError.Builder self() {
            return this;
        }
    }
}
