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

import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.messenger.dto.Addition;
import de.sovity.edc.extension.messenger.dto.Answer;
import de.sovity.edc.extension.messenger.dto.Multiplication;
import de.sovity.edc.extension.messenger.dto.UnsupportedMessage;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SovityMessengerExtensionE2eTest {
    private static final String EMITTER_PARTICIPANT_ID = "emitter";
    private static final String RECEIVER_PARTICIPANT_ID = "receiver";

    @RegisterExtension
    static EdcExtension emitterEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension receiverEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase EMITTER_DATABASE = new TestDatabaseViaTestcontainers();
    @RegisterExtension
    static final TestDatabase RECEIVER_DATABASE = new TestDatabaseViaTestcontainers();

    private ConnectorConfig providerConfig;
    private ConnectorConfig consumerConfig;

    private String counterPartyAddress;

    @BeforeEach
    void setup() {
        providerConfig = forTestDatabase(EMITTER_PARTICIPANT_ID, EMITTER_DATABASE);
        emitterEdcContext.setConfiguration(providerConfig.getProperties());
        emitterEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

        consumerConfig = forTestDatabase(RECEIVER_PARTICIPANT_ID, RECEIVER_DATABASE);
        receiverEdcContext.setConfiguration(consumerConfig.getProperties());
        receiverEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

        counterPartyAddress = consumerConfig.getProtocolEndpoint().getUri().toString();
    }

    @Test
    void e2eTest() throws ExecutionException, InterruptedException, TimeoutException {
        val sovityMessenger = emitterEdcContext.getContext().getService(SovityMessenger.class);
        val handlers = receiverEdcContext.getContext().getService(SovityMessengerRegistry.class);
        handlers.register(Addition.class, in -> new Answer(in.getOp1() + in.getOp2()));
        handlers.register(Multiplication.class, in -> new Answer(in.getOp1() * in.getOp2()));

        val added = sovityMessenger.send(Answer.class, counterPartyAddress, new Addition(20, 30));
        val multiplied = sovityMessenger.send(Answer.class, counterPartyAddress, new Multiplication(20, 30));

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
    void e2eNoHandlerTest() {
        val sovityMessenger = emitterEdcContext.getContext().getService(SovityMessenger.class);

        val added = sovityMessenger.send(Answer.class, counterPartyAddress, new UnsupportedMessage());

        // assert
        val exception = assertThrows(ExecutionException.class, () -> added.get(30, SECONDS));
        assertThat(exception.getCause()).isInstanceOf(SovityMessengerException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("No handler for message type unsupported");
    }

}
