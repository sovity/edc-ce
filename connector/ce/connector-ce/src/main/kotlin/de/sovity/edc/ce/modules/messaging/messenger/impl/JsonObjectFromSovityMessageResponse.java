/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

public class JsonObjectFromSovityMessageResponse extends AbstractJsonLdTransformer<SovityMessageResponse, JsonObject> {

    public JsonObjectFromSovityMessageResponse() {
        super(SovityMessageResponse.class, JsonObject.class);
    }

    @Override
    public @Nullable JsonObject transform(
        @NotNull SovityMessageResponse message,
        @NotNull TransformerContext context) {

        var builder = Json.createObjectBuilder();
        builder.add(TYPE, Prop.SovityMessageExt.RESPONSE)
            .add(Prop.SovityMessageExt.HEADER, message.header())
            .add(Prop.SovityMessageExt.BODY, message.body());

        return builder.build();
    }
}
