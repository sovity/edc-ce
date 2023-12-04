package de.sovity.edc.extension.jwks.controller;

import com.nimbusds.jose.jwk.JWK;
import de.sovity.edc.extension.jwks.jwk.VaultJwkFactory;
import jakarta.ws.rs.core.Response;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwksControllerTest {

    @Test
    void loadingJwkSucceeds() {
        // given
        var monitor = mock(Monitor.class);
        var vaultJkwFactory = mock(VaultJwkFactory.class);
        var jwksJsonTransformer = mock(JwksJsonTransformer.class);
        var jwksController = new JwksController(
                vaultJkwFactory,
                jwksJsonTransformer,
                "test",
                monitor);
        var jwk = mock(JWK.class);
        var jsonResponse = "jsonResponse";

        // when
        when(vaultJkwFactory.publicX509JwkFromAlias("test")).thenReturn(jwk);
        when(jwksJsonTransformer.toJwksJson(jwk)).thenReturn(jsonResponse);
        var jwksResponse = jwksController.getJwks();

        // then
        verify(vaultJkwFactory).publicX509JwkFromAlias("test");
        verify(jwksJsonTransformer).toJwksJson(jwk);
        assertEquals(
                Response.Status.OK.getStatusCode(),
                jwksResponse.getStatus());
        assertEquals(jsonResponse, jwksResponse.getEntity());
    }

    @Test
    void loadingJwkFails() {
        // given
        var monitor = mock(Monitor.class);
        var vaultJkwFactory = mock(VaultJwkFactory.class);
        var jwksJsonTransformer = mock(JwksJsonTransformer.class);
        var jwksController = new JwksController(
                vaultJkwFactory,
                jwksJsonTransformer,
                "test",
                monitor);
        var exceptionMessage = "message";

        // when
        when(vaultJkwFactory.publicX509JwkFromAlias("test")).thenThrow(new EdcException(exceptionMessage));
        var jwksResponse = jwksController.getJwks();

        // then
        verify(monitor).warning(String.format(
                JwksController.JWKS_RESPONSE_FAILED_MESSAGE_TEMPLATE,
                exceptionMessage));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                jwksResponse.getStatus());
    }

    @Test
    void loadingJwkFailsNoAliasConfigured() {
        // given
        var monitor = mock(Monitor.class);
        var vaultJkwFactory = mock(VaultJwkFactory.class);
        var jwksJsonTransformer = mock(JwksJsonTransformer.class);
        var jwksController = new JwksController(
                vaultJkwFactory,
                jwksJsonTransformer,
                null,
                monitor);

        // when
        var jwksResponse = jwksController.getJwks();

        // then
        verify(monitor).warning(String.format(
                JwksController.JWKS_RESPONSE_FAILED_MESSAGE_TEMPLATE,
                JwksController.ALIAS_NOT_SET_MESSAGE));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                jwksResponse.getStatus());
    }

}