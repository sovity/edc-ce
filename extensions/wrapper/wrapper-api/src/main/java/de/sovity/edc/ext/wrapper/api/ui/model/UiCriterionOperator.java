/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Contract Definition Criterion
 *
 * @see <pre>org.eclipse.edc.connector.defaults.storage.CriterionToPredicateConverterImpl</pre>
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Operator for constraints", enumAsRef = true)
public enum UiCriterionOperator {
    EQ,
    IN,
    LIKE;
}
