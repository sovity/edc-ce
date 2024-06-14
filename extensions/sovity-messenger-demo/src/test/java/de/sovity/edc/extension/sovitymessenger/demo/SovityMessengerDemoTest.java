package de.sovity.edc.extension.sovitymessenger.demo;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
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

    // Setup, you may skip this part

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

    private String receiverAddress;

    // still setup, skip

    @BeforeEach
    void setup() {
        providerConfig = forTestDatabase(EMITTER_PARTICIPANT_ID, EMITTER_DATABASE);
        emitterEdcContext.setConfiguration(providerConfig.getProperties());
        emitterEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

        consumerConfig = forTestDatabase(RECEIVER_PARTICIPANT_ID, RECEIVER_DATABASE);
        receiverEdcContext.setConfiguration(consumerConfig.getProperties());
        receiverEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

        receiverAddress = "http://localhost:" + consumerConfig.getProtocolEndpoint().port() + consumerConfig.getProtocolEndpoint().path();
    }

    /**
     * Actual usage of the Sovity Messenger.
     */
    @Test
    void demo() throws ExecutionException, InterruptedException, TimeoutException {
        /*
         * Get a reference to the SovityMessenger. This is equivalent to
         *
         * @Inject SovityMessenger messenger;
         *
         * in an extension.
         *
         * This messenger is already configured to accept messages in de.sovity.edc.extension.sovitymessenger.demo.SovityMessengerDemo#initialize
         */
        val messenger = emitterEdcContext.getContext().getService(SovityMessenger.class);

        // Select the target EDC

        // Send messages
        val added = messenger.send(Answer.class, receiverAddress, new Addition(20, 30));
        val rooted = messenger.send(Answer.class, receiverAddress, new Sqrt(9.0));
        val unregistered = messenger.send(Answer.class, receiverAddress, new UnregisteredMessage());

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
