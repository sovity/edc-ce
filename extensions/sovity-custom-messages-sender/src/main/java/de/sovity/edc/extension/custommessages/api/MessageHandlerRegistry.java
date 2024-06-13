package de.sovity.edc.extension.custommessages.api;

import java.util.function.Function;

public interface MessageHandlerRegistry {
    @SuppressWarnings("unchecked")
    default <IN, OUT> void register(String type, Function<IN, OUT> handler, IN... reified) {
        register(getClassOf(reified), type, handler);
    }

    <IN, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler);

    <IN, OUT> Handler<IN, OUT> getHandler(String type);

    record Handler<IN, OUT>(Class<IN> clazz, GenericMessageHandler<IN, OUT> handler) {
    }

    private static <T> Class<T> getClassOf(T[] array) {
        return (Class<T>) array.getClass().getComponentType();
    }
}
