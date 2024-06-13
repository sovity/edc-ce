package de.sovity.edc.extension.custommessages.impl;

import de.sovity.edc.extension.custommessages.echo.SovityMessageRecord;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

public class JsonObjectFromGenericSovityMessage extends AbstractJsonLdTransformer<SovityMessageRecord, JsonObject> {

    public JsonObjectFromGenericSovityMessage() {
        super(SovityMessageRecord.class, JsonObject.class);
    }

    @Override
    public @Nullable JsonObject transform(
        @NotNull SovityMessageRecord message,
        @NotNull TransformerContext context) {

        var builder = Json.createObjectBuilder();
        builder.add(TYPE, Prop.SovityMessageExt.MESSAGE)
            .add(Prop.SovityMessageExt.HEADER, message.header())
            .add(Prop.SovityMessageExt.BODY, message.body());

        return builder.build();
    }
}
