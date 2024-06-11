package de.sovity.edc.extension.custommessages;

import de.sovity.edc.extension.custommessages.echo.EchoMessage;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Provides(SovityMessageDispatcher.class)
public class CustomMessagesExtension implements ServiceExtension {

    public static final String NAME = "SovityCustomMessages";
    private static final String TYPE_MANAGER_SERIALIZER_KEY = "sovity-custom";

    @Inject
    private TypeManager typeManager;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    @Inject
    private Monitor monitor;

    @Inject
    private EdcHttpClient httpClient;

    private SovityMessageDispatcher sovityMessageDispatcher;

    @Override
    public void initialize(ServiceExtensionContext context) {
        sovityMessageDispatcher = new SovityMessageDispatcherImpl(httpClient);
        context.registerService(SovityMessageDispatcher.class, sovityMessageDispatcher);

        dispatcherRegistry.register(sovityMessageDispatcher);

        try {
            dispatcherRegistry.dispatch(
                EchoMessage.Response.class,
                new EchoMessage(
                    new URI("http://edc2/management-api/v2/sovity/message/generic").toURL(), "Hi!"));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new EdcException(e);
        }
    }
}
