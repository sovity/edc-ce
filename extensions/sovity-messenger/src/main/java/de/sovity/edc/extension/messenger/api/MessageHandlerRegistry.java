package de.sovity.edc.extension.messenger.api;

import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * The component where to register message handlers when using the {@link SovityMessenger}.
 */
public interface MessageHandlerRegistry {
    /**
     * Register a handler to process a sovity message.
     * Prefer using {@link MessageHandlerRegistry#register(Class, String, Function)} for a typesafe, non-string-based registration.
     *
     * @param type The type
     * @param handler
     * @param reified
     * @param <IN>
     * @param <OUT>
     */
    @SuppressWarnings("unchecked")
    default <IN, OUT> void register(String type, Function<IN, OUT> handler, IN... reified) {
        register(getClassOf(reified), type, handler);
    }

    @SuppressWarnings("unchecked")
    default <IN, OUT> void register(Class<? extends SovityMessage> messageClass, Function<IN, OUT> handler, IN... reified) {
        try {
            val defaultConstructor = messageClass.getConstructor();
            defaultConstructor.setAccessible(true);
            val type = defaultConstructor.newInstance().getType();
            register(type, handler);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    <IN, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler);

    <IN, OUT> Handler<IN, OUT> getHandler(String type);

    record Handler<IN, OUT>(Class<IN> clazz, Function<IN, OUT> handler) {
    }

    private static <T> Class<T> getClassOf(T[] array) {
        return (Class<T>) array.getClass().getComponentType();
    }
}
