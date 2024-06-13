package de.sovity.edc.extension.custommessages.api;

@FunctionalInterface
public interface GenericMessageHandler<IN, OUT> {
    OUT handle(IN in);
}
