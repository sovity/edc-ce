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

package de.sovity.edc.extension.jwks.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;

import java.util.Optional;

@RequiredArgsConstructor
public class VaultJwkFactory {

    public static final String RESOLVE_ALIAS_FROM_VAULT_FAILED_MESSAGE =
            "Could not resolve PEM-Encoded-X509-Certificate for alias %s";
    public static final String PARSE_VALUE_FROM_VAULT_FAILED_MESSAGE =
            "Could not parse PEM-Encoded-X509-Certificate for alias %s, Reason: %s";
    private final Vault vault;

    public JWK publicX509JwkFromAlias(String alias) {
        return Optional
                .ofNullable(vault.resolveSecret(alias))
                .map(pemString -> parseX509Cert(pemString, alias))
                .orElseThrow(() -> new EdcException(String.format(RESOLVE_ALIAS_FROM_VAULT_FAILED_MESSAGE, alias)));
    }

    private JWK parseX509Cert(String pem, String alias) {
        try {
            return JWK.parseFromPEMEncodedX509Cert(pem);
        } catch (JOSEException e) {
            throw new EdcException(String.format(
                    PARSE_VALUE_FROM_VAULT_FAILED_MESSAGE,
                    alias,
                    e.getMessage()));
        }
    }

}
