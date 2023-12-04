/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.extension.jwks.controller;

import com.nimbusds.jose.jwk.JWK;
import jakarta.json.Json;

public class JwksJsonTransformerImpl implements JwksJsonTransformer {

    @Override
    public String toJwksJson(JWK jwk) {
        var jwkJsonObject = Json.createObjectBuilder(jwk.toJSONObject());
        var jwksJsonArray = Json.createArrayBuilder()
                .add(jwkJsonObject)
                .build();
        return Json.createObjectBuilder()
                .add("keys", jwksJsonArray)
                .build().toString();
    }
}
