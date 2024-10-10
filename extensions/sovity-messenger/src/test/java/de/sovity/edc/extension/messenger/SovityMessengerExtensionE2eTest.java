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
 *
 */

package de.sovity.edc.extension.messenger;

import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.messenger.dto.Addition;
import de.sovity.edc.extension.messenger.dto.Answer;
import de.sovity.edc.extension.messenger.dto.Multiplication;
import de.sovity.edc.extension.messenger.dto.UnsupportedMessage;
import de.sovity.edc.utils.config.ConfigUtils;
import lombok.val;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class SovityMessengerExtensionE2eTest {
    @RegisterExtension
    static CeE2eTestExtension extension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:utils:edc-integration-test")
        .skipDb(true)
        .build();
    private String counterPartyAddress;
    private String counterPartyId;

    @BeforeEach
    void setup(@Consumer Config consumerConfig) {
        counterPartyAddress = ConfigUtils.getProtocolApiUrl(consumerConfig);
        counterPartyId = ConfigUtils.getParticipantId(consumerConfig);
    }

    @Test
    void e2eTest(
        @Provider SovityMessenger providerMessenger,
        @Consumer SovityMessengerRegistry consumerHandlers
    ) throws ExecutionException, InterruptedException, TimeoutException {
        consumerHandlers.register(Addition.class, in -> new Answer(in.getOp1() + in.getOp2()));
        consumerHandlers.register(Multiplication.class, in -> new Answer(in.getOp1() * in.getOp2()));

        val added = providerMessenger.send(Answer.class, counterPartyAddress, counterPartyId, new Addition(20, 30));
        val multiplied = providerMessenger.send(Answer.class, counterPartyAddress, counterPartyId, new Multiplication(20, 30));

        // assert
        added.get(30, SECONDS)
            .onFailure(it -> fail(it.getFailureDetail()))
            .onSuccess(it -> {
                assertThat(it).isInstanceOf(Answer.class);
                assertThat(it.getAnswer()).isEqualTo(50);
            });

        multiplied.get(30, SECONDS)
            .onFailure(it -> fail(it.getFailureDetail()))
            .onSuccess(it -> {
                assertThat(it).isInstanceOf(Answer.class);
                assertThat(it.getAnswer()).isEqualTo(600);
            });
    }

    @Test
    void e2eNoHandlerTest(
        @Provider SovityMessenger sovityMessenger
    ) {
        val added = sovityMessenger.send(Answer.class, counterPartyAddress, counterPartyId, new UnsupportedMessage());

        // assert
        val exception = assertThrows(ExecutionException.class, () -> added.get(30, SECONDS));
        assertThat(exception.getCause()).isInstanceOf(SovityMessengerException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("No handler for message type unsupported");
    }

}
