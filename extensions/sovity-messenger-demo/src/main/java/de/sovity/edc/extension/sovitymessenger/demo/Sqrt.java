package de.sovity.edc.extension.sovitymessenger.demo;


import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;

public class Sqrt implements SovityMessage {

    @Override
    public String getType() {
        return "sqrt";
    }

    public Sqrt() {
    }

    public Sqrt(double a) {
        this.a = a;
    }

    @JsonProperty("value")
    private double a;

    public double getA() {
        return a;
    }
}
