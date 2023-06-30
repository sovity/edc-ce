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

package de.sovity.edc.ext.brokerserver.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Broker Server API",
                version = "0.0.0",
                description = "Broker Server API for the Broker Server built by sovity.",
                contact = @Contact(
                        name = "sovity GmbH",
                        email = "contact@sovity.de",
                        url = "https://github.com/sovity/edc-broker-server-extension/issues/new/choose"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://github.com/sovity/edc-broker-server-extension/blob/main/LICENSE"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Broker Server API in sovity/edc-broker-server-extension",
                url = "https://github.com/sovity/edc-broker-server-extension/tree/main/extensions/broker-server-api"
        )
)
public interface ApiInformation {
}
