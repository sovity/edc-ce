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

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Supported Data Sink Types by UiDataSink:
         - `HTTP_DATA_PUSH`: This EDC's data plane pushes the data into the given data sink, putting the payload as request body.
         - `HTTP_DATA_PROXY`: Once the transfer is in the status STARTED, you can call a 2nd endpoint to receive the EDR.
            The EDR will contain an URL you can call as a proxy to the data source. Which parts of the HTTP call are passed
            on is decided by the configuration of parameterization on the data source, e.g. an added path, method, body or query params.
         - `CUSTOM`: Provide custom transfer type, data sink properties and transfer properties to use a
           transfer type that is not fully integrated into the API Wrapper yet.
    """, enumAsRef = true)
public enum UiInitiateTransferType {
    HTTP_DATA_PUSH,
    HTTP_DATA_PROXY,
    CUSTOM
}

