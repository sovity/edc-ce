package de.sovity.edc.extension.custommessages.echo;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.sovity.edc.extension.custommessages.impl.ObjectMapperFactory;
import de.sovity.edc.extension.custommessages.impl.SovityMessageRecord;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.net.MalformedURLException;
import java.net.URL;

class SovityMessageRecordTest {


    @Test
    void canSerialize() throws MalformedURLException, JsonProcessingException, JSONException {
        // arrange
        val message = new SovityMessageRecord(
            new URL("https://example.com"),
            "{\"type\":\"foo\"}",
            "body content"
        );

        val mapper = new ObjectMapperFactory().createObjectMapper();

        // act
        val serialized = mapper.writeValueAsString(message);

        // assert
        JSONAssert.assertEquals(
            """
                {
                  "https://semantic.sovity.io/message/generic/header": "{\\"type\\":\\"foo\\"}",
                  "https://semantic.sovity.io/message/generic/body": "body content"
                }
                """,
            serialized,
            JSONCompareMode.STRICT
        );
    }
}
