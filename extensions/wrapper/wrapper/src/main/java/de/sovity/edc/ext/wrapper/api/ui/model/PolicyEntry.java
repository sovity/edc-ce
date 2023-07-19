/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.Prohibition;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Policy Entry for Policy Page")
public class PolicyEntry {
    @Schema(description = "List of Permissions", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Permission> permissions;

    @Schema(description = "List of Prohibitions", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Prohibition> prohibitions;

    @Schema(description = "List of Duties", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Duty> obligations;

    @Schema(description = "Extensible Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> extensibleProperties;

    @Schema(description = "Inherits From")
    private String inheritsFrom;

    @Schema(description = "Assigner")
    private String assigner;

    @Schema(description = "Assignee")
    private String assignee;

    @Schema(description = "Target")
    private String target;

    @JsonProperty("@type")
    @Schema(description = "Policy Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyType type;
}
