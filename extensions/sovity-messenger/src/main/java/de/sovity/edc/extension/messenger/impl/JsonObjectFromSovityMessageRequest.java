package de.sovity.edc.extension.messenger.impl;

import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

public class JsonObjectFromSovityMessageRequest extends AbstractJsonLdTransformer<SovityMessageRequest, JsonObject> {

    public JsonObjectFromSovityMessageRequest() {
        super(SovityMessageRequest.class, JsonObject.class);
    }

    @Override
    public @Nullable JsonObject transform(
        @NotNull SovityMessageRequest message,
        @NotNull TransformerContext context) {

        var builder = Json.createObjectBuilder();
        builder.add(TYPE, Prop.SovityMessageExt.REQUEST)
            .add(Prop.SovityMessageExt.HEADER, message.header())
            .add(Prop.SovityMessageExt.BODY, message.body());

        return builder.build();
    }
}
