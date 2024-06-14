package de.sovity.edc.extension.sovitymessenger.demo.message;

import de.sovity.edc.extension.messenger.api.SovityMessage;

public class UnregisteredMessage implements SovityMessage {

    @Override
    public String getType() {
        return "UNREGISTERED";
    }
}
