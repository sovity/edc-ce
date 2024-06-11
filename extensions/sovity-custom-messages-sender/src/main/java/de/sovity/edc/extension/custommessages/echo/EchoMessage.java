package de.sovity.edc.extension.custommessages.echo;

import de.sovity.edc.extension.custommessages.SovityExtendedProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record EchoMessage(
    URL counterPartyAddress,
    String content
) implements RemoteMessage {

    public record Response(String content) {
        // TODO: how to JsonLd?
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
