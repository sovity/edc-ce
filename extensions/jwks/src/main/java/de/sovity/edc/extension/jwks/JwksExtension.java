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

package de.sovity.edc.extension.jwks;

import de.sovity.edc.extension.jwks.controller.JwksController;
import de.sovity.edc.extension.jwks.controller.JwksJsonTransformerImpl;
import de.sovity.edc.extension.jwks.jwk.VaultJwkFactoryImpl;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;


public class JwksExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "JwksExtension";
    public static final String TOKEN_VERIFIER_PUBLIC_KEY_ALIAS =
            "edc.transfer.proxy.token.verifier.publickey.alias";
    @Inject
    private WebService webService;
    @Inject
    private Vault vault;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        var pemSecretAlias = context.getSetting(TOKEN_VERIFIER_PUBLIC_KEY_ALIAS, null);
        if (pemSecretAlias == null) {
            monitor.warning(() -> "No vault alias provided for JWKS-Extension");
        }
        var controller = new JwksController(
                new VaultJwkFactoryImpl(vault),
                new JwksJsonTransformerImpl(),
                pemSecretAlias,
                monitor);
        webService.registerResource(controller);
    }

}
