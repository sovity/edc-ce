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
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "HTTP_DATA type Data Source. This is an HttpData-PUSH data sink. Note that consumption of parameterized data sources is not supported via HttpData-PUSH")
public class UiDataSinkHttpDataPush {
    @Schema(
        description = "HTTP Request Method",
        defaultValue = "GET",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSinkHttpDataPushMethod method;

    @Schema(
        description = "HTTP Request URL.",
        example = "https://my-app.my-org.com/api/edc-data-offer/v1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String baseUrl;

    @Schema(
        description = "HTTP Request Query Params / Query String.",
        example = "search=example&limit=10",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String queryString;


    @Schema(
        description = "The authentication method.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiHttpPushAuth auth;

    @Schema(
        description = "HTTP Request Headers.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, String> headers;
}
