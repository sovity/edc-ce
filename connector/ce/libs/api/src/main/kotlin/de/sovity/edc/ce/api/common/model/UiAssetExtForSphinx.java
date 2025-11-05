/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
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
 *     Fraunhofer ISST - initial implementation of an unified workflow for creating data offerings
 */
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Type-safe extra fields for the sphin-x dataspace.")
public class UiAssetExtForSphinx {

    @Schema(description = "Patient Count", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String patientCount;

    @Schema(description = "Birth Year Min", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthYearMin;

    @Schema(description = "Birth Year Max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthYearMax;

    @Schema(description = "Administrative Gender", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String administrativeGender;

    @Schema(description = "Body Height Min", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bodyHeightMin;

    @Schema(description = "Body Height Max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bodyHeightMax;

    @Schema(description = "Primary Diagnosis", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String diagnosisPrimary;

    @Schema(description = "Secondary Diagnosis", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String diagnosisSecondary;

    @Schema(description = "Encounter Start Year", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String encounterStart;

    @Schema(description = "Encounter End Year", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String encounterEnd;

    @Schema(description = "Medication Count", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String medicationCount;

    @Schema(description = "Dosage Count", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dosageCount;

    @Schema(description = "Clinical Specialty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clinicalSpecialty;

}
