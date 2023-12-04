package de.sovity.edc.extension.jwks.jwk;

import com.nimbusds.jose.jwk.JWK;

public interface VaultJwkFactory {

    String RESOLVE_ALIAS_FROM_VAULT_FAILED_MESSAGE =
            "Could not resolve PEM-Encoded-X509-Certificate for alias %s";

    String PARSE_VALUE_FROM_VAULT_FAILED_MESSAGE =
            "Could not parse PEM-Encoded-X509-Certificate for alias %s, Reason: %s";

    JWK publicX509JwkFromAlias(String alias);


}
