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
 * Submit Request for creating a new User Managed Vault Secret
 * @export
 * @interface VaultSecretCreateSubmit
 */
export interface VaultSecretCreateSubmit {
    /**
     * Key
     * @type {string}
     * @memberof VaultSecretCreateSubmit
     */
    key: string;
    /**
     * Value
     * @type {string}
     * @memberof VaultSecretCreateSubmit
     */
    value: string;
    /**
     * Description
     * @type {string}
     * @memberof VaultSecretCreateSubmit
     */
    description: string;
}

/**
 * Check if a given object implements the VaultSecretCreateSubmit interface.
 */
export function instanceOfVaultSecretCreateSubmit(value: any): value is VaultSecretCreateSubmit {
    if (!('key' in value) || value['key'] === undefined) return false;
    if (!('value' in value) || value['value'] === undefined) return false;
    if (!('description' in value) || value['description'] === undefined) return false;
    return true;
}

export function VaultSecretCreateSubmitFromJSON(json: any): VaultSecretCreateSubmit {
    return VaultSecretCreateSubmitFromJSONTyped(json, false);
}

export function VaultSecretCreateSubmitFromJSONTyped(json: any, ignoreDiscriminator: boolean): VaultSecretCreateSubmit {
    if (json == null) {
        return json;
    }
    return {
        
        'key': json['key'],
        'value': json['value'],
        'description': json['description'],
    };
}

export function VaultSecretCreateSubmitToJSON(json: any): VaultSecretCreateSubmit {
    return VaultSecretCreateSubmitToJSONTyped(json, false);
}

export function VaultSecretCreateSubmitToJSONTyped(value?: VaultSecretCreateSubmit | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'key': value['key'],
        'value': value['value'],
        'description': value['description'],
    };
}

