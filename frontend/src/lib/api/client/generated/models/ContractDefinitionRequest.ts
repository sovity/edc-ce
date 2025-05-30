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

import { mapValues } from '../runtime';
import type { UiCriterion } from './UiCriterion';
import {
    UiCriterionFromJSON,
    UiCriterionFromJSONTyped,
    UiCriterionToJSON,
    UiCriterionToJSONTyped,
} from './UiCriterion';

/**
 * Data for creating a Contract Definition
 * @export
 * @interface ContractDefinitionRequest
 */
export interface ContractDefinitionRequest {
    /**
     * Contract Definition ID
     * @type {string}
     * @memberof ContractDefinitionRequest
     */
    contractDefinitionId: string;
    /**
     * Contract Policy ID
     * @type {string}
     * @memberof ContractDefinitionRequest
     */
    contractPolicyId: string;
    /**
     * Access Policy ID
     * @type {string}
     * @memberof ContractDefinitionRequest
     */
    accessPolicyId: string;
    /**
     * List of Criteria for the contract
     * @type {Array<UiCriterion>}
     * @memberof ContractDefinitionRequest
     */
    assetSelector: Array<UiCriterion>;
}

/**
 * Check if a given object implements the ContractDefinitionRequest interface.
 */
export function instanceOfContractDefinitionRequest(value: any): value is ContractDefinitionRequest {
    if (!('contractDefinitionId' in value) || value['contractDefinitionId'] === undefined) return false;
    if (!('contractPolicyId' in value) || value['contractPolicyId'] === undefined) return false;
    if (!('accessPolicyId' in value) || value['accessPolicyId'] === undefined) return false;
    if (!('assetSelector' in value) || value['assetSelector'] === undefined) return false;
    return true;
}

export function ContractDefinitionRequestFromJSON(json: any): ContractDefinitionRequest {
    return ContractDefinitionRequestFromJSONTyped(json, false);
}

export function ContractDefinitionRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): ContractDefinitionRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'contractDefinitionId': json['contractDefinitionId'],
        'contractPolicyId': json['contractPolicyId'],
        'accessPolicyId': json['accessPolicyId'],
        'assetSelector': ((json['assetSelector'] as Array<any>).map(UiCriterionFromJSON)),
    };
}

export function ContractDefinitionRequestToJSON(json: any): ContractDefinitionRequest {
    return ContractDefinitionRequestToJSONTyped(json, false);
}

export function ContractDefinitionRequestToJSONTyped(value?: ContractDefinitionRequest | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'contractDefinitionId': value['contractDefinitionId'],
        'contractPolicyId': value['contractPolicyId'],
        'accessPolicyId': value['accessPolicyId'],
        'assetSelector': ((value['assetSelector'] as Array<any>).map(UiCriterionToJSON)),
    };
}

