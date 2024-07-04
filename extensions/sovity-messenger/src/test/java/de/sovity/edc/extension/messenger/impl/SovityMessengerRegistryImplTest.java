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

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.SovityMessage;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SovityMessengerRegistryImplTest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class MyInt implements SovityMessage {

        @Override
        public String getType() {
            return "message";
        }

        @JsonProperty
        private int value;
    }

    @Test
    void canRegisterAndRetrieveHandler() {
        // arrange
        SovityMessengerRegistry handlers = new SovityMessengerRegistry();
        Function<MyInt, String> handler = myInt -> String.valueOf(myInt.getValue());

        // act
        handlers.register(MyInt.class, "itoa", handler);
        val back = handlers.getHandler("itoa");

        // assert
        assertThat(back.handler().apply(ClaimToken.Builder.newInstance().build(), new MyInt(1))).isEqualTo("1");
    }

    @Test
    void register_whenRegisteringDuplicatedName_shouldThrowIllegalStateException() {
        // arrange
        SovityMessengerRegistry handlers = new SovityMessengerRegistry();
        Function<MyInt, String> handler = myInt -> String.valueOf(myInt.getValue());

        // act
        handlers.register(MyInt.class, "foo", handler);

        // assert
        assertThrows(IllegalStateException.class, () -> handlers.register(MyInt.class, "foo", handler));
    }
}
