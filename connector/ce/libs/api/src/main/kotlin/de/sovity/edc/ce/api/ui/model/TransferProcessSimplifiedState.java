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
package de.sovity.edc.ce.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class exists because we want to highlight either running or failed transfer processes in our UI.
 * <p>
 * That distinction has to be made somewhere. Let's rather do that distinction in the backend.
 */
@Schema(description = "Simplified Transfer Process State to be used in UI", enumAsRef = true)
public enum TransferProcessSimplifiedState {
    RUNNING,
    OK,
    ERROR
}
