package de.sovity.edc.extension.sovitymessenger.demo;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;
import de.sovity.edc.extension.messenger.api.SovityMessenger;
import de.sovity.edc.extension.sovitymessenger.demo.message.Addition;
import de.sovity.edc.extension.sovitymessenger.demo.message.Answer;
import de.sovity.edc.extension.sovitymessenger.demo.message.Sqrt;
import de.sovity.edc.extension.sovitymessenger.demo.message.UnregisteredMessage;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;

class SovityMessengerDemoTest {

    // Setup

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
        new ConnectorRemote(fromConnectorConfig(providerConfig)); // TODO: needed?

        consumerConfig = forTestDatabase(RECEIVER_PARTICIPANT_ID, RECEIVER_DATABASE);
        receiverEdcContext.setConfiguration(consumerConfig.getProperties());
        receiverEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);
        new ConnectorRemote(fromConnectorConfig(consumerConfig)); // TODO: needed?

        counterPartyAddress = "http://localhost:" + consumerConfig.getProtocolEndpoint().port() + consumerConfig.getProtocolEndpoint().path();
    }

    /**
     * Actual usage of the Sovity Messenger.
     */
    @Test
    void demo() throws ExecutionException, InterruptedException, TimeoutException {
        /*
         * Equivalent to
         *
         * @Inject SovityMessenger messenger;
         *
         * This messenger is already configured to accept some messages in de.sovity.edc.extension.sovitymessenger.demo.SovityMessengerDemo#initialize
         */
        val messenger = emitterEdcContext.getContext().getService(SovityMessenger.class);
        /*
         * This is where the receiver can register its message handlers.
         * Here we can see that there is a handler to process the Addition message.
         */
        val handlers = receiverEdcContext.getContext().getService(MessageHandlerRegistry.class);
        val handler = handlers.getHandler(Addition.class);
        System.out.println("The handler exists: " + handler);


        // Select the target EDC

        // Send messages
        val added = messenger.send(Answer.class, counterPartyAddress, new Addition(20, 30));
        val rooted = messenger.send(Answer.class, counterPartyAddress, new Sqrt(9.0));
        val unregistered = messenger.send(Answer.class, counterPartyAddress, new UnregisteredMessage());

        // Wait for the answers
        added.get(2, TimeUnit.SECONDS).onSuccess(it -> System.out.println(it.getAnswer()));
        rooted.get(2, TimeUnit.SECONDS).onSuccess(it -> System.out.println(it.getAnswer()));

        try {
            unregistered.get();
        } catch (ExecutionException e) {
            /*
             * When a problem happens, a SovityMessenger exception is thrown and encapsulated in an ExecutionException.
             */
            System.out.println(e.getCause().getMessage());
        }
    }

}
