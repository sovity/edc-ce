package de.sovity.edc.extension.sovitymessenger.demo;

import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;
import de.sovity.edc.extension.messenger.api.SovityMessenger;
import de.sovity.edc.extension.sovitymessenger.demo.message.Addition;
import de.sovity.edc.extension.sovitymessenger.demo.message.Answer;
import de.sovity.edc.extension.sovitymessenger.demo.message.Sqrt;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.function.Function;

import static java.lang.Math.sqrt;


public class SovityMessengerDemo implements ServiceExtension {

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
    private MessageHandlerRegistry registry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        // Register the various messages that you would like to process.
        registry.register(Sqrt.class, (Function<Sqrt, Answer>) single -> new Answer(sqrt(single.getValue())));
        registry.register(Addition.class, (Function<Addition, Answer>) add -> new Answer(add.op1 + add.op2));

        /*
         * In the counterpart connector, messages can be sent with the code below.
         * Check out the de.sovity.edc.extension.sovitymessenger.demo.SovityMessengerDemoTest#demo()
         * for a detailed usage.
         */

        // val answer = sovityMessenger.send(Answer.class, "http://localhost/api/dsp", new Sqrt(9.0));
    }
}
