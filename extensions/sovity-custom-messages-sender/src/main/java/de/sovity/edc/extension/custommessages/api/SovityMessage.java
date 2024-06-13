package de.sovity.edc.extension.custommessages.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SovityMessage {
    /**
     * @return A unique string to identify the type of message.
     */
    @JsonIgnore
    String getType();
}
