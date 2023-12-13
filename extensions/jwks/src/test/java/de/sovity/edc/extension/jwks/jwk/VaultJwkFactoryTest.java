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

import com.nimbusds.jose.jwk.RSAKey;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static de.sovity.edc.extension.jwks.jwk.VaultJwkFactory.PARSE_VALUE_FROM_VAULT_FAILED_MESSAGE;
import static de.sovity.edc.extension.jwks.jwk.VaultJwkFactory.RESOLVE_ALIAS_FROM_VAULT_FAILED_MESSAGE;
import static de.sovity.edc.extension.jwks.util.TestCertFromFileUtil.getCertStringFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VaultJwkFactoryTest {

    private static final String PARSE_CERTIFICATE_FAILED_MESSAGE =
            "Couldn't parse PEM-encoded X.509 certificate";
    private static final String JWK_ALIAS = "jwk-alias";

    @Mock
    private Vault vault;

    @Test
    void jwkFromAliasSuccessful() throws IOException {
        // arrange
        var vaultJwkFactory = new VaultJwkFactory(vault);
        var certString = getCertStringFromFile();
        when(vault.resolveSecret(JWK_ALIAS)).thenReturn(certString);

        // act
        var jwk = vaultJwkFactory.publicX509JwkFromAlias(JWK_ALIAS);

        // assert
        assertNotNull(jwk);
        assertEquals("360586573322806545473834353174745870260060531097", jwk.getKeyID());
        assertEquals(1701353600000L, jwk.getNotBeforeTime().getTime());
        assertEquals(4854953600000L, jwk.getExpirationTime().getTime());
        assertEquals("RSA", jwk.getKeyType().getValue());
        assertEquals(
                "P-dbyBaTkocsAKpv0Lx3JHaOTEyPOclVNOdoi-hQ75o",
                jwk.getX509CertSHA256Thumbprint().toString());
        assertEquals("AQAB", ((RSAKey) jwk).getPublicExponent().toString());
        assertNotNull(jwk.getX509CertChain());
    }

    @Test
    void jwkFromAliasVaultJwkNull() {
        // arrange
        var vaultJwkFactory = new VaultJwkFactory(vault);
        when(vault.resolveSecret(JWK_ALIAS)).thenReturn(null);

        // act & assert
        var edcException = assertThrows(
                EdcException.class,
                () -> vaultJwkFactory.publicX509JwkFromAlias(JWK_ALIAS));
        var message = String.format(RESOLVE_ALIAS_FROM_VAULT_FAILED_MESSAGE, JWK_ALIAS);
        assertEquals(message, edcException.getMessage());
    }

    @Test
    void jwkFromAliasVaultInvalidValue() {
        // arrange
        var vaultJksFactory = new VaultJwkFactory(vault);
        when(vault.resolveSecret(JWK_ALIAS)).thenReturn("invalid-value");

        // act & assert
        var edcException = assertThrows(
                EdcException.class,
                () -> vaultJksFactory.publicX509JwkFromAlias(JWK_ALIAS));
        var message = String.format(
                PARSE_VALUE_FROM_VAULT_FAILED_MESSAGE,
                JWK_ALIAS,
                PARSE_CERTIFICATE_FAILED_MESSAGE);
        assertEquals(message, edcException.getMessage());
    }
}