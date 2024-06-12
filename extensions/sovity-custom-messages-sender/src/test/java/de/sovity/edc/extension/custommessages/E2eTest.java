package de.sovity.edc.extension.custommessages;

import de.sovity.edc.extension.custommessage.receiver.SovityCustomMessageReceiverExtension;
import de.sovity.edc.extension.custommessages.echo.EchoMessage;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class E2eTest {
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

    private ConnectorRemote emitterConnector;
    private ConnectorRemote receiverConnector;

//    private EdcClient providerClient;
//    private EdcClient consumerClient;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(EMITTER_PARTICIPANT_ID, 21000, EMITTER_DATABASE);
        emitterEdcContext.setConfiguration(providerConfig.getProperties());
        emitterConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        emitterEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

//        providerClient = EdcClient.builder()
//            .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
//            .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
//            .build();

        var consumerConfig = forTestDatabase(RECEIVER_PARTICIPANT_ID, 23000, RECEIVER_DATABASE);
        receiverEdcContext.setConfiguration(consumerConfig.getProperties());
//        receiverEdcContext.registerSystemExtension(SovityCustomMessageReceiverExtension.class, new SovityCustomMessageReceiverExtension());
        receiverConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        receiverEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);

//        consumerClient = EdcClient.builder()
//            .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
//            .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
//            .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
//        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void e2eAuthTest() throws URISyntaxException, MalformedURLException {
        val dispatcher = emitterEdcContext.getContext().getService(RemoteMessageDispatcherRegistry.class);

        val future = dispatcher.dispatch(
            EchoMessage.Response.class,
            new EchoMessage(
                new URI("http://localhost:" + 23002 + "/api/management").toURL(), "Hi!"));

        // assert
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            future.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> assertThat(it.content()).isEqualTo("Hi!"));
        });
    }
}
