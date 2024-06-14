package de.sovity.edc.extension.messenger.impl;

import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MessageHandlerRegistryImpl implements MessageHandlerRegistry {

    private Map<String, Handler<?, ?>> handlers = new HashMap<>();


    @SuppressWarnings("unchecked")
    @Override
    public <IN, OUT> Handler<IN, OUT> getHandler(String type) {
        // TODO: fallback when not present
        return (Handler<IN, OUT>) handlers.get(type);
    }

    @Override
    public <IN, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler) {
        // TODO: warn when replacing a handler for the same type
        handlers.put(type, new Handler<>(clazz, handler::apply));
    }
}
