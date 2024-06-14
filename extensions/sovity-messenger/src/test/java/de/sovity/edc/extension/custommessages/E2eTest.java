package de.sovity.edc.extension.custommessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.custommessages.api.SovityMessageException;
import de.sovity.edc.extension.custommessages.api.SovityMessenger;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    static class UnsupportedMessage implements SovityMessage {
        @Override
        public String getType() {
            return getClass().getCanonicalName();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class Addition implements SovityMessage {
        @Override
        public String getType() {
            return "add";
        }

        @JsonProperty("a")
        private int a;
        @JsonProperty("b")
        private int b;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class Multiplication implements SovityMessage {
        @Override
        public String getType() {
            return "mul";
        }

        @JsonProperty("a")
        private int a;
        @JsonProperty("b")
        private int b;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Answer implements SovityMessage {
        @Override
        public String getType() {
            return getClass().getCanonicalName();
        }

        @JsonProperty("answer")
        private int answer;
    }

    @Test
    void e2eTest() {
        val sovityMessenger = emitterEdcContext.getContext().getService(SovityMessenger.class);
        val handlers = receiverEdcContext.getContext().getService(MessageHandlerRegistry.class);
        handlers.register("add", (Function<Addition, Answer>) in -> new Answer(in.getA() + in.getB()));
        handlers.register("mul", (Function<Addition, Answer>) in -> new Answer(in.getA() * in.getB()));

        // TODO: no need to tell the destination address, it's always on the DSP port
        val counterPartyAddress = "http://localhost:" + consumerConfig.getProtocolEndpoint().port() + consumerConfig.getProtocolEndpoint().path();
        val added = sovityMessenger.send(Answer.class, counterPartyAddress, new Addition(20, 30));
        val multiplied = sovityMessenger.send(Answer.class, counterPartyAddress, new Multiplication(20, 30));

        // assert
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            added.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> {
                    assertThat(it).isInstanceOf(Answer.class);
                    assertThat(it.getAnswer()).isEqualTo(50);
                });
        });

        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            multiplied.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> {
                    assertThat(it).isInstanceOf(Answer.class);
                    assertThat(it.getAnswer()).isEqualTo(600);
                });
        });
    }

    @Test
    void e2eNoHandlerTest() {
        val sovityMessenger = emitterEdcContext.getContext().getService(SovityMessenger.class);

        val added = sovityMessenger.send(Answer.class, counterPartyAddress, new Addition(20, 30));

        // assert
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            val exception = assertThrows(ExecutionException.class, added::get);
            assertThat(exception.getCause()).isInstanceOf(SovityMessageException.class);
        });

    }

    // TODO: test unsupported messages
}
