package de.sovity.edc.extension.sovitymessenger.demo;

import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;
import de.sovity.edc.extension.messenger.api.SovityMessenger;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.function.Function;


public class SovityMessengerDemo implements ServiceExtension {

    public static final String NAME = "sovityMessengerDemo";

    @Inject
    private SovityMessenger sovityMessenger;

    @Inject
    private MessageHandlerRegistry registry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        registry.register(Sqrt.class, (Function<Sqrt, Answer>) single -> new Answer(Math.sqrt(single.getA())));

        val answer = sovityMessenger.send(Answer.class, "http://localhost/api/dsp", new Sqrt(9.0));
    }
}
