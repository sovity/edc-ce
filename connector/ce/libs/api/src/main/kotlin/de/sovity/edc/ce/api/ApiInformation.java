/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "sovity EDC API Wrapper",
        version = "0.0.0",
        description = "sovity's EDC API Wrapper contains a selection of APIs for multiple consumers, " +
            "e.g. our EDC UI API, our generic Use Case API, our Commercial Edition APIs, etc. " +
            "We bundled these APIs, so we can have an easier time generating our API Client Libraries.",
        contact = @Contact(
            name = "sovity GmbH",
            email = "contact@sovity.de",
            url = "https://github.com/sovity/edc-ce/issues/new/choose"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://github.com/sovity/edc-ce/blob/main/LICENSE"
        )
    ),
    servers = {
        @Server(url = "https://my-connector/api/management")
    },
    externalDocs = @ExternalDocumentation(
        description = "EDC API Wrapper Project in sovity/edc-ce",
        url = "https://github.com/sovity/edc-ce/tree/main/extensions/wrapper"
    )
)
public interface ApiInformation {
}
