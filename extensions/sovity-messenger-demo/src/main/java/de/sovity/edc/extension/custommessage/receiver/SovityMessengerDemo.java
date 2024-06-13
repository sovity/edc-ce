package de.sovity.edc.extension.custommessage.receiver;

import de.sovity.edc.extension.custommessage.receiver.message.Answer;
import de.sovity.edc.extension.custommessage.receiver.message.Sqrt;
import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import de.sovity.edc.extension.custommessages.api.PostOffice;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.function.Function;


public class SovityMessengerDemo implements ServiceExtension {

    public static final String NAME = "sovityMessengerDemo";

    @Inject
    private PostOffice postOffice;

    @Inject
    private MessageHandlerRegistry registry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        registry.register(Sqrt.class, (Function<Sqrt, Answer>) single -> new Answer(Math.sqrt(single.getA())));

        val answer = postOffice.send(Answer.class, "http://localhost/api/dsp", new Sqrt(9.0));
    }
}
