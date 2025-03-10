/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The interface to implement when sending a message via the {@link SovityMessenger}.
 * <br>
 * The classes extending this interface must annotate the private fields to be sent with Jackson's
 * {@link com.fasterxml.jackson.annotation.JsonProperty}.
 * {@code public} fields are serialized automatically.
 * <br>
 * It is recommended to have a no-args constructor.
 * <br>
 * See <a href="https://www.baeldung.com/jackson-field-serializable-deserializable-or-not">this doc</a>
 * for more detailed info about Jackson's serialization.
 */
public interface SovityMessage {
    /**
     * To avoid conflicts, it is recommended to use Java package-like naming convention.
     *
     * @return A unique string across all possible messages to identify the type of message.
     */
    @JsonIgnore
    String getType();
}
