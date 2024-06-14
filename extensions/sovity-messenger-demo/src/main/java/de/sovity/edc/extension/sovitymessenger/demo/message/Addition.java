package de.sovity.edc.extension.sovitymessenger.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;

import static de.sovity.edc.extension.sovitymessenger.demo.message.Common.ROOT;

public class Addition implements SovityMessage {

    public Addition() {
    }

    public Addition(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String getType() {
        return ROOT + "add";
    }

    public int a;

    public int b;

    @JsonProperty("a")
    public int getA() {
        return a;
    }

    @JsonProperty("b")
    public int getB() {
        return b;
    }
}
