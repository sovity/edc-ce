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

package de.sovity.edc.extension.messenger.demo;

import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.RuntimePerClassWithDbExtension;
import de.sovity.edc.extension.messenger.SovityMessenger;
import de.sovity.edc.extension.messenger.SovityMessengerException;
import de.sovity.edc.extension.messenger.demo.message.Addition;
import de.sovity.edc.extension.messenger.demo.message.Answer;
import de.sovity.edc.extension.messenger.demo.message.Counterparty;
import de.sovity.edc.extension.messenger.demo.message.Failing;
import de.sovity.edc.extension.messenger.demo.message.Signal;
import de.sovity.edc.extension.messenger.demo.message.Sqrt;
import de.sovity.edc.extension.messenger.demo.message.UnregisteredMessage;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

class SovityMessengerDemoTest {

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void demo() {
        /*
         * Get a reference to the SovityMessenger. This is equivalent to
         *
         * @Inject SovityMessenger messenger;
         *
         * in an extension.
         *
         * This messenger is already configured to accept messages in de.sovity.edc.extension.messenger.demo.SovityMessengerDemo#initialize
         */
        val messenger = emitterExtension.getRuntimePerClassExtensionFixed().getService(SovityMessenger.class);

        System.out.println("START MARKER");

        // Send messages
        val added = messenger.send(Answer.class, receiverAddress, receiverId, new Addition(20, 30));
        val rooted = messenger.send(Answer.class, receiverAddress, receiverId, new Sqrt(9.0));
        val withClaims = messenger.send(Answer.class, receiverAddress, receiverId, new Counterparty());
        val unregistered = messenger.send(Answer.class, receiverAddress, receiverId, new UnregisteredMessage());
        messenger.send(receiverAddress, receiverId, new Signal());

        try {
            // Wait for the answers
            added.get(2, SECONDS).onSuccess(it -> System.out.println(it.getAnswer()));
            rooted.get(2, SECONDS).onSuccess(it -> System.out.println(it.getAnswer()));
            withClaims.get(2, SECONDS);
            unregistered.get(2, SECONDS);
        } catch (ExecutionException e) {
            /*
             * When a problem happens, a SovityMessengerException is thrown and encapsulated in an ExecutionException.
             */
            System.out.println(e.getCause().getMessage());
        }

        try {
            val failing1 = messenger.send(Answer.class, receiverAddress, receiverId, new Failing("Some content 1"));
            val failing2 = messenger.send(Answer.class, receiverAddress, receiverId, new Failing("Some content 2"));
            failing1.get(2, SECONDS);
            failing2.get(2, SECONDS);
        } catch (ExecutionException e) {
            val cause = e.getCause();
            if (cause instanceof SovityMessengerException messengerException) {
                // Error when processing a message with type demo-failing
                System.out.println(messengerException.getMessage());
                // {"message":"Some content 1/2"}
                System.out.println(messengerException.getBody());
            }
        }

        System.out.println("END MARKER");
    }

    @RegisterExtension
    static RuntimePerClassWithDbExtension emitterExtension = new RuntimePerClassWithDbExtension(
        ":launchers:connectors:sovity-dev",
        "emitter",
        testDatabase -> {
            ConnectorConfig emitterConfig = CeIntegrationTestUtils.defaultConfig("emitter", testDatabase);
            return emitterConfig.getProperties();
        }
    );

    private static String receiverId = "receiver";

    private static ConnectorConfig receiverConfig;

    @RegisterExtension
    static RuntimePerClassWithDbExtension receiverExtension = new RuntimePerClassWithDbExtension(
        ":launchers:connectors:sovity-dev",
        "receiver",
        testDatabase -> {
            receiverConfig = CeIntegrationTestUtils.defaultConfig(receiverId, testDatabase);
            return receiverConfig.getProperties();
        }
    );

    private String receiverAddress;

    @BeforeEach
    void setup() {
        receiverAddress = receiverConfig.getProtocolApiUrl();
    }
}
