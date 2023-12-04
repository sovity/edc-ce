package de.sovity.edc.extension.jwks.controller;

import com.nimbusds.jose.jwk.JWK;

public interface JwksJsonTransformer {
    String toJwksJson(JWK jwk);
}
