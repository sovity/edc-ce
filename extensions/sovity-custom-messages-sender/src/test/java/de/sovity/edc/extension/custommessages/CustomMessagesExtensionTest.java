package de.sovity.edc.extension.custommessages;

import de.sovity.edc.extension.custommessages.echo.EchoMessage;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.stop.Stop.stopQuietly;

@ExtendWith(EdcExtension.class)
class CustomMessagesExtensionTest {

    private final static String managementApiPath = "/management-api";
    private final int httpPort = getFreePort();
    private final int dataPort = getFreePort();

    private int testPort = getFreePort();
    private ClientAndServer mockServer;

    @BeforeEach
    void beforeEach(EdcExtension extension) {
        extension.registerServiceMock(TokenDecorator.class, (td) -> td.scope("test-scope"));
        extension.setConfiguration(Map.of(
            "web.http.port", String.valueOf(httpPort),
            "web.http.path", "/api",
            "web.http.management.port", String.valueOf(dataPort),
            "web.http.management.path", managementApiPath,
            "edc.api.auth.key", "ApiKey"
        ));

        mockServer = ClientAndServer.startClientAndServer(testPort);
    }

    @AfterEach
    public void stopServer() {
        stopQuietly(mockServer);
    }

    @Test
    void canSendAndReceiveMessage(EdcExtension extension) throws URISyntaxException, MalformedURLException {
        // arrange
        mockServer.when(request("/some/path/sovity/message/echo"))
            .respond(it -> new HttpResponse()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withBody("""
                    {
                        "http://example.com/pong": "Hi! :)"
                    }
                    """));
        val dispatcherRegistry = extension.getContext().getService(RemoteMessageDispatcherRegistry.class);

        // act
        val future = dispatcherRegistry.dispatch(
            EchoMessage.Response.class,
            new EchoMessage(
                new URI("http://localhost:" + testPort + "/some/path").toURL(), "Hi!"));

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            // assert
            future.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> assertThat(it.content()).isEqualTo("Hi! :)"));
        });

    }

    @Test
    void authenticationTokenIsPresent(EdcExtension extension) throws URISyntaxException, MalformedURLException {
        // arrange
        mockServer.when(request("/some/path/sovity/message/echo"))
            .respond(it -> {
                if (!it.containsHeader("Authorization")) {
                    fail("Missing auth header");
                }
                return new HttpResponse()
                    .withStatusCode(HttpStatusCode.OK_200.code())
                    .withBody("""
                        {
                            "http://example.com/pong": "Hi! :)"
                        }
                        """);
            });
        val dispatcherRegistry = extension.getContext().getService(RemoteMessageDispatcherRegistry.class);

        // act
        val future = dispatcherRegistry.dispatch(
            EchoMessage.Response.class,
            new EchoMessage(
                new URI("http://localhost:" + testPort + "/some/path").toURL(), "Hi!"));

        // assert
        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            future.get()
                .onFailure(it -> fail(it.getFailureDetail()))
                .onSuccess(it -> assertThat(it.content()).isEqualTo("Hi! :)"));
        });
    }
}
