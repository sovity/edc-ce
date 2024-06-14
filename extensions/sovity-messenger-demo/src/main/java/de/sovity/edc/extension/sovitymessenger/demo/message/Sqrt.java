package de.sovity.edc.extension.sovitymessenger.demo.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;

import static de.sovity.edc.extension.sovitymessenger.demo.message.Common.ROOT;

public class Sqrt implements SovityMessage {

    private final String TYPE = ROOT + "sqrt";

    @Override
    public String getType() {
        return TYPE;
    }

    public Sqrt() {
    }

    public Sqrt(double a) {
        this.a = a;
    }

    private double a;

    @JsonProperty("value")
    public double getA() {
        return a;
    }
}
