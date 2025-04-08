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
 * Enabled UI Features
 */
@Schema(description = "Enabled UI Features", enumAsRef = true)
public enum UiConfigFeature {
    // Enterprise Edition specific attribute to view limits enforced on consuming contract agreements
    CONNECTOR_LIMITS,

    // Enables marketing for sovity in open-source variants
    OPEN_SOURCE_MARKETING,

    // Enterprise Edition specific flag to enable marketing for other Enterprise Edition variants in basic connectors
    EE_BASIC_MARKETING,
}
