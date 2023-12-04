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

import de.sovity.edc.extension.jwks.JwksExtension;
import de.sovity.edc.extension.jwks.jwk.VaultJwkFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Objects;

@Produces({MediaType.APPLICATION_JSON})
@Path(JwksController.JWKS_PATH)
public class JwksController {

    static final String ALIAS_NOT_SET_MESSAGE = String.format(
            "No alias for JWKS-Extension configured. Please set the %s property",
            JwksExtension.TOKEN_VERIFIER_PUBLIC_KEY_ALIAS);
    static final String JWKS_RESPONSE_FAILED_MESSAGE_TEMPLATE =
            "Creating JWKS response failed: %s";
    public static final String JWKS_PATH = "/jwks";
    private final VaultJwkFactory vaultJkwFactory;
    private final JwksJsonTransformer jwksJsonTransformer;
    private final String pemSecretAlias;
    private final Monitor monitor;

    public JwksController(
            VaultJwkFactory vaultJkwFactory,
            JwksJsonTransformer jwksJsonTransformer,
            String pemSecretAlias,
            Monitor monitor) {
        this.vaultJkwFactory = vaultJkwFactory;
        this.jwksJsonTransformer = jwksJsonTransformer;
        this.pemSecretAlias = pemSecretAlias;
        this.monitor = monitor;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJwks() {
        try {
            validateAliasSet(pemSecretAlias);
            var jwk = vaultJkwFactory.publicX509JwkFromAlias(pemSecretAlias);
            return Response
                    .ok(jwksJsonTransformer.toJwksJson(jwk), MediaType.APPLICATION_JSON)
                    .build();
        } catch (EdcException e) {
            monitor.warning(String.format(JWKS_RESPONSE_FAILED_MESSAGE_TEMPLATE, e.getMessage()));
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private void validateAliasSet(String pemSecretAlias) {
        if (Objects.isNull(pemSecretAlias) || pemSecretAlias.isBlank()) {
            throw new EdcException(ALIAS_NOT_SET_MESSAGE);
        }
    }
}
