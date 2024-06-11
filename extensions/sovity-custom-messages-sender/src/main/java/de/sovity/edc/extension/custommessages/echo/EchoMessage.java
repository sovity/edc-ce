package de.sovity.edc.extension.custommessages.echo;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.SovityExtendedProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record EchoMessage(
    URL counterPartyAddress,
    @JsonProperty("http://example.com/ping") String content
) implements RemoteMessage {

    public record Response(@JsonProperty("http://example.com/pong") String content) {
    }

    @Override
    public String getProtocol() {
        return SovityExtendedProtocol.SOVITY_EXTENDED_PROTOCOL;
    }

    @Override
    public String getCounterPartyAddress() {
        return counterPartyAddress.toString();
    }
}
