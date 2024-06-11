package de.sovity.edc.extension.custommessages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class DummySerializer<T> extends JsonSerializer<T> {
    private final Class<T> type;

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        // write new object
        var serializer = instantiateSerializerFromProvider(provider, type);
        // TODO: what should be serialized exactly?
        //  What's the correct format?
        //  Which  parts of JsonLd should applied?
    }

    private JsonSerializer<Object> instantiateSerializerFromProvider(SerializerProvider provider, Class<T> type) throws JsonMappingException {
        var javaType = provider.constructType(type);
        var beanDescription = provider.getConfig().introspect(javaType);
        var staticTyping = provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
        return BeanSerializerFactory.instance.findBeanOrAddOnSerializer(provider, javaType, beanDescription, staticTyping);
    }
}
