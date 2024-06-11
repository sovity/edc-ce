package de.sovity.edc.extension.custommessages;

import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EdcExtension.class)
class CustomMessagesExtensionTest {

    private final static String managementApiPath = "/management-api";
    private final int httpPort = getFreePort();
    private final int dataPort = getFreePort();

    @BeforeEach
    void beforeEach(EdcExtension extension) {
        extension.setConfiguration(Map.of(
            "web.http.port", String.valueOf(httpPort),
            "web.http.path", "/api",
            "web.http.management.port", String.valueOf(dataPort),
            "web.http.management.path", managementApiPath,
            "edc.api.auth.key", "ApiKey"
        ));
    }

    @Test
    void getAndEchoAnswer_whenSendingAnEchoMessage(EdcExtension extension) {
        // arrange
        extension.

        // act

        // assert
    }
}
