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
    Ui Policy Expression types:
    * `CONSTRAINT` - Expression 'a=b'
    * `AND` - Conjunction of several expressions. Evaluates to true iff all child expressions are true.
    * `OR` - Disjunction of several expressions. Evaluates to true iff at least one child expression is true.
    * `XONE` - XONE operation. Evaluates to true iff exactly one child expression is true.
    """, enumAsRef = true)
public enum UiPolicyExpressionType {
    EMPTY,
    CONSTRAINT,
    AND,
    OR,
    XONE
}


