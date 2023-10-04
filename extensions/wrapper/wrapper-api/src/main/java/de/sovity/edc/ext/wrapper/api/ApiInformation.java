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

package de.sovity.edc.ext.wrapper.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "sovity EDC API Wrapper",
                version = "0.0.0",
                description = "sovity's EDC API Wrapper contains a selection of APIs for multiple consumers, " +
                        "e.g. our EDC UI API, our generic Use Case API, our Commercial APIs, etc. " +
                        "We bundled these APIs, so we can have an easier time generating our API Client Libraries.",
                contact = @Contact(
                        name = "Sovity GmbH",
                        email = "contact@sovity.de",
                        url = "https://github.com/sovity/edc-extensions/issues/new/choose"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://github.com/sovity/edc-extensions/blob/main/LICENSE"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "EDC API Wrapper Project in sovity/edc-extensions",
                url = "https://github.com/sovity/edc-extensions/tree/main/extensions/wrapper"
        )
)
public interface ApiInformation {
}
