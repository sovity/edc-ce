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
import type { UiPolicyCreateRequest } from './UiPolicyCreateRequest';
import {
    UiPolicyCreateRequestFromJSON,
    UiPolicyCreateRequestFromJSONTyped,
    UiPolicyCreateRequestToJSON,
    UiPolicyCreateRequestToJSONTyped,
} from './UiPolicyCreateRequest';

/**
 * [Deprecated] Create a Policy Definition. Use PolicyDefinitionCreateDto
 * @export
 * @interface PolicyDefinitionCreateRequest
 */
export interface PolicyDefinitionCreateRequest {
    /**
     * Policy Definition ID
     * @type {string}
     * @memberof PolicyDefinitionCreateRequest
     */
    policyDefinitionId: string;
    /**
     * [Deprecated] Conjunction of constraints (simplified UiPolicyExpression)
     * @type {UiPolicyCreateRequest}
     * @memberof PolicyDefinitionCreateRequest
     * @deprecated
     */
    policy: UiPolicyCreateRequest;
}

/**
 * Check if a given object implements the PolicyDefinitionCreateRequest interface.
 */
export function instanceOfPolicyDefinitionCreateRequest(value: any): value is PolicyDefinitionCreateRequest {
    if (!('policyDefinitionId' in value) || value['policyDefinitionId'] === undefined) return false;
    if (!('policy' in value) || value['policy'] === undefined) return false;
    return true;
}

export function PolicyDefinitionCreateRequestFromJSON(json: any): PolicyDefinitionCreateRequest {
    return PolicyDefinitionCreateRequestFromJSONTyped(json, false);
}

export function PolicyDefinitionCreateRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): PolicyDefinitionCreateRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'policyDefinitionId': json['policyDefinitionId'],
        'policy': UiPolicyCreateRequestFromJSON(json['policy']),
    };
}

export function PolicyDefinitionCreateRequestToJSON(json: any): PolicyDefinitionCreateRequest {
    return PolicyDefinitionCreateRequestToJSONTyped(json, false);
}

export function PolicyDefinitionCreateRequestToJSONTyped(value?: PolicyDefinitionCreateRequest | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'policyDefinitionId': value['policyDefinitionId'],
        'policy': UiPolicyCreateRequestToJSON(value['policy']),
    };
}

