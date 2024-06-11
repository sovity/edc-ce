package de.sovity.edc.extension.custommessage.receiver;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.StringReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

@ExtendWith(EdcExtension.class)
public class SovityCustomMessageReceiverExtensionTest {

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
    void echoApiCall_whenGivenAnInput_shouldReturnIt() {
        // arrange
        val request = RestAssured.given()
            .port(dataPort)
            .body("""
                {
                    "http://example.com/content": "stuff"
                }
                """)
            .contentType(ContentType.JSON)
            .post("http://localhost:" + dataPort + managementApiPath + "/sovity/message/echo");

        // act
        val response = request
            .then()
            .statusCode(Response.Status.OK.getStatusCode());

        val body = response.extract().body().asString();

        // assert
        val json = Json.createReader(new StringReader(body)).readObject();
        assertThat(json.getString("http://example.com/content")).isEqualTo("echo stuff");
    }
}
