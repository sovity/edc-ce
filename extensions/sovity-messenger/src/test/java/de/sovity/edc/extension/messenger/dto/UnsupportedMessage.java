package de.sovity.edc.extension.messenger.dto;

import de.sovity.edc.extension.messenger.api.SovityMessage;

public class UnsupportedMessage implements SovityMessage {
    @Override
    public String getType() {
        return getClass().getCanonicalName();
    }
}
