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
/* eslint-disable */
/**
 * sovity EDC API Wrapper
 * sovity\'s EDC API Wrapper contains a selection of APIs for multiple consumers, e.g. our EDC UI API, our generic Use Case API, our Commercial Edition APIs, etc. We bundled these APIs, so we can have an easier time generating our API Client Libraries.
 *
 * The version of the OpenAPI document: 0.0.0
 * Contact: contact@sovity.de
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * Operator for constraints
 * @export
 */
export const UiCriterionOperator = {
    Eq: 'EQ',
    In: 'IN',
    Like: 'LIKE'
} as const;
export type UiCriterionOperator = typeof UiCriterionOperator[keyof typeof UiCriterionOperator];


export function instanceOfUiCriterionOperator(value: any): boolean {
    for (const key in UiCriterionOperator) {
        if (Object.prototype.hasOwnProperty.call(UiCriterionOperator, key)) {
            if (UiCriterionOperator[key as keyof typeof UiCriterionOperator] === value) {
                return true;
            }
        }
    }
    return false;
}

export function UiCriterionOperatorFromJSON(json: any): UiCriterionOperator {
    return UiCriterionOperatorFromJSONTyped(json, false);
}

export function UiCriterionOperatorFromJSONTyped(json: any, ignoreDiscriminator: boolean): UiCriterionOperator {
    return json as UiCriterionOperator;
}

export function UiCriterionOperatorToJSON(value?: UiCriterionOperator | null): any {
    return value as any;
}

export function UiCriterionOperatorToJSONTyped(value: any, ignoreDiscriminator: boolean): UiCriterionOperator {
    return value as UiCriterionOperator;
}

