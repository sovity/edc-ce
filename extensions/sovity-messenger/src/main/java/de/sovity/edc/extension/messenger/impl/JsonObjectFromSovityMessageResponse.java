/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.messenger.impl;

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
