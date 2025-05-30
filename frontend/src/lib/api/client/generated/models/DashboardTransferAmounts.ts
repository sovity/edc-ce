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
/**
 * Number of Transfer Processes for given direction.
 * @export
 * @interface DashboardTransferAmounts
 */
export interface DashboardTransferAmounts {
    /**
     * Number of Transfer Processes
     * @type {number}
     * @memberof DashboardTransferAmounts
     */
    numTotal: number;
    /**
     * Number of running Transfer Processes
     * @type {number}
     * @memberof DashboardTransferAmounts
     */
    numRunning: number;
    /**
     * Number of successful Transfer Processes
     * @type {number}
     * @memberof DashboardTransferAmounts
     */
    numOk: number;
    /**
     * Number of failed Transfer Processes
     * @type {number}
     * @memberof DashboardTransferAmounts
     */
    numError: number;
}

/**
 * Check if a given object implements the DashboardTransferAmounts interface.
 */
export function instanceOfDashboardTransferAmounts(value: any): value is DashboardTransferAmounts {
    if (!('numTotal' in value) || value['numTotal'] === undefined) return false;
    if (!('numRunning' in value) || value['numRunning'] === undefined) return false;
    if (!('numOk' in value) || value['numOk'] === undefined) return false;
    if (!('numError' in value) || value['numError'] === undefined) return false;
    return true;
}

export function DashboardTransferAmountsFromJSON(json: any): DashboardTransferAmounts {
    return DashboardTransferAmountsFromJSONTyped(json, false);
}

export function DashboardTransferAmountsFromJSONTyped(json: any, ignoreDiscriminator: boolean): DashboardTransferAmounts {
    if (json == null) {
        return json;
    }
    return {
        
        'numTotal': json['numTotal'],
        'numRunning': json['numRunning'],
        'numOk': json['numOk'],
        'numError': json['numError'],
    };
}

export function DashboardTransferAmountsToJSON(json: any): DashboardTransferAmounts {
    return DashboardTransferAmountsToJSONTyped(json, false);
}

export function DashboardTransferAmountsToJSONTyped(value?: DashboardTransferAmounts | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'numTotal': value['numTotal'],
        'numRunning': value['numRunning'],
        'numOk': value['numOk'],
        'numError': value['numError'],
    };
}

