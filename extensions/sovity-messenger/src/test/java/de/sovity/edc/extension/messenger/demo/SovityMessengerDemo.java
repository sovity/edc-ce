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

import de.sovity.edc.extension.messenger.SovityMessenger;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import de.sovity.edc.extension.messenger.demo.message.Addition;
import de.sovity.edc.extension.messenger.demo.message.Answer;
import de.sovity.edc.extension.messenger.demo.message.Counterparty;
import de.sovity.edc.extension.messenger.demo.message.Failing;
import de.sovity.edc.extension.messenger.demo.message.Signal;
import de.sovity.edc.extension.messenger.demo.message.Sqrt;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static java.lang.Math.sqrt;


public class SovityMessengerDemo implements ServiceExtension {

    @Inject
    private ParticipantAgentService participantAgentService;

    public static final String NAME = "sovityMessengerDemo";

    @Override
    public String name() {
        return NAME;
    }

    /*
     * 3 parts are needed:
     * - the messenger
     * - the handler registry
     * - your handlers
     */

    @Inject
    private SovityMessenger sovityMessenger;

    @Inject
    private SovityMessengerRegistry registry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        // Register the various messages that you would like to process.
        // By class, safer.
        registry.register(Sqrt.class, single -> new Answer(sqrt(single.getValue())));
        // By String, could be unsafe during refactorings.
        registry.register(Addition.class, Addition.TYPE, add -> new Answer(add.op1 + add.op2));

        registry.registerSignal(Signal.class, signal -> System.out.println("Received signal."));
        registry.register(Failing.class, failing -> {
            throw new RuntimeException("Failed!");
        });


        registry.register(Counterparty.class, (claims, counterparty) -> {
            val agent = participantAgentService.createFor(claims);
            System.out.println("Message sent from " + agent.getIdentity());
            return new Answer();
        });

        /*
         * In the counterpart connector, messages can be sent with the code below.
         * Check out the de.sovity.edc.extension.sovitymessenger.demo.SovityMessengerDemoTest#demo()
         * for a detailed usage.
         */

        // val answer = sovityMessenger.send(Answer.class, "http://localhost/api/dsp", new Sqrt(9.0));
    }
}
