package de.sovity.extension.clearinghouse.sender.message;

import de.sovity.extension.clearinghouse.sender.message.clearingdispatcher.ExtendedMessageProtocolClearing;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URI;
import java.net.URL;

public record LogMessage(URL clearingHouseLogUrl,
                         URI connectorBaseUrl,
                         Object eventToLog) implements RemoteMessage {
    @Override
    public String getProtocol() {
        return ExtendedMessageProtocolClearing.IDS_EXTENDED_PROTOCOL_CLEARING;
    }

    @Override
    public String getConnectorAddress() {
        return clearingHouseLogUrl.toString();
    }
}
