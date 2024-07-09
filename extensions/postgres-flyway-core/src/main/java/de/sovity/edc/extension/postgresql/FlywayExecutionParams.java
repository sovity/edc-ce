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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.extension.postgresql;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Builder
@Value
@With
public class FlywayExecutionParams {
    @Builder.Default
    Consumer<String> infoLogger = System.out::println;

    @Builder.Default
    List<String> migrationLocations = new ArrayList<>();

    @Builder.Default
    boolean migrate = false;

    @Builder.Default
    boolean tryRepairOnFailedMigration = false;

    @Builder.Default
    boolean cleanEnabled = false;

    @Builder.Default
    boolean clean = false;

    @Builder.Default
    String table = "flyway_schema_history";
}
