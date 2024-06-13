package de.sovity.edc.extension.custommessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.PostOffice;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

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

    private ConnectorConfig providerConfig;
    private ConnectorConfig consumerConfig;

    @BeforeEach
    void setup() {
        providerConfig = forTestDatabase(EMITTER_PARTICIPANT_ID, 21000, EMITTER_DATABASE);
        emitterEdcContext.setConfiguration(providerConfig.getProperties());
        emitterEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);
        emitterConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

//        providerClient = EdcClient.builder()
//            .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
//            .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
//            .build();

        consumerConfig = forTestDatabase(RECEIVER_PARTICIPANT_ID, 23000, RECEIVER_DATABASE);
        receiverEdcContext.setConfiguration(consumerConfig.getProperties());
        receiverEdcContext.registerServiceMock(TokenDecorator.class, (td) -> td);
        receiverConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

//        consumerClient = EdcClient.builder()
//            .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
//            .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
//            .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
//        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    static class UnsupportedMessage implements SovityMessage {
        @Override
        public String getType() {
            return getClass().getCanonicalName();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Query implements SovityMessage {
        @Override
        public String getType() {
            return getClass().getCanonicalName();
        }

        @JsonProperty("stuff")
        private int stuff = 1;
    }

    @Getter
    static class Answer implements SovityMessage {
        @Override
        public String getType() {
            return getClass().getCanonicalName();
        }

        @JsonProperty("stuff")
        private int stuff = 0;
    }

    @Test
    void e2eAuthTest() throws URISyntaxException, MalformedURLException {
        val postOffice = emitterEdcContext.getContext().getService(PostOffice.class);

        val counterPartyAddress = "http://localhost:" + consumerConfig.getManagementEndpoint().port() + consumerConfig.getManagementEndpoint().path();
        val answer = postOffice.send(Answer.class, counterPartyAddress, new Query(100));

        // assert
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            answer.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> assertThat(it.getStuff()).isEqualTo(100));
        });
    }
}
