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

package de.sovity.edc.extension.messenger.impl;

import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageHandlerRegistryImplTest {

    @Test
    void canRegisterAndRetrieveHandler() {
        // arrange
        MessageHandlerRegistry handlers = new MessageHandlerRegistryImpl();
        Function<Integer, String> handler = String::valueOf;

        // act
        handlers.register("itoa", handler);
        val back = handlers.getHandler("itoa");

        // assert
        assertThat(back.handler().apply(1)).isEqualTo("1");
    }

    @Test
    void register_whenRegisteringDuplicatedName_shouldThrowIllegalStateException() {
        // arrange
        MessageHandlerRegistry handlers = new MessageHandlerRegistryImpl();
        Function<Integer, String> handler = String::valueOf;

        // act
        handlers.register("foo", handler);

        // assert
        assertThrows(IllegalStateException.class, () -> handlers.register("foo", handler));
    }
}
