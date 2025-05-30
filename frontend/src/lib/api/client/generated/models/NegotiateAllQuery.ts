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
import type { AssetFilterConstraint } from './AssetFilterConstraint';
import {
    AssetFilterConstraintFromJSON,
    AssetFilterConstraintFromJSONTyped,
    AssetFilterConstraintToJSON,
    AssetFilterConstraintToJSONTyped,
} from './AssetFilterConstraint';
import type { CallbackAddressDto } from './CallbackAddressDto';
import {
    CallbackAddressDtoFromJSON,
    CallbackAddressDtoFromJSONTyped,
    CallbackAddressDtoToJSON,
    CallbackAddressDtoToJSONTyped,
} from './CallbackAddressDto';

/**
 * Negotiates all given assets by the given filter
 * @export
 * @interface NegotiateAllQuery
 */
export interface NegotiateAllQuery {
    /**
     * Target EDC DSP endpoint URL
     * @type {string}
     * @memberof NegotiateAllQuery
     */
    connectorEndpoint: string;
    /**
     * Target EDC Participant ID
     * @type {string}
     * @memberof NegotiateAllQuery
     */
    participantId: string;
    /**
     * Data offers to negotiate. Filter expressions, joined by AND. If left empty, all data offers are negotiated
     * @type {Array<AssetFilterConstraint>}
     * @memberof NegotiateAllQuery
     */
    filter: Array<AssetFilterConstraint>;
    /**
     * Optional limit to limit the number of assets negotiated to
     * @type {number}
     * @memberof NegotiateAllQuery
     */
    limit?: number;
    /**
     * Optionally listen to negotiation success / failure for new negotiations
     * @type {Array<CallbackAddressDto>}
     * @memberof NegotiateAllQuery
     */
    callbackAddresses?: Array<CallbackAddressDto>;
}

/**
 * Check if a given object implements the NegotiateAllQuery interface.
 */
export function instanceOfNegotiateAllQuery(value: any): value is NegotiateAllQuery {
    if (!('connectorEndpoint' in value) || value['connectorEndpoint'] === undefined) return false;
    if (!('participantId' in value) || value['participantId'] === undefined) return false;
    if (!('filter' in value) || value['filter'] === undefined) return false;
    return true;
}

export function NegotiateAllQueryFromJSON(json: any): NegotiateAllQuery {
    return NegotiateAllQueryFromJSONTyped(json, false);
}

export function NegotiateAllQueryFromJSONTyped(json: any, ignoreDiscriminator: boolean): NegotiateAllQuery {
    if (json == null) {
        return json;
    }
    return {
        
        'connectorEndpoint': json['connectorEndpoint'],
        'participantId': json['participantId'],
        'filter': ((json['filter'] as Array<any>).map(AssetFilterConstraintFromJSON)),
        'limit': json['limit'] == null ? undefined : json['limit'],
        'callbackAddresses': json['callbackAddresses'] == null ? undefined : ((json['callbackAddresses'] as Array<any>).map(CallbackAddressDtoFromJSON)),
    };
}

export function NegotiateAllQueryToJSON(json: any): NegotiateAllQuery {
    return NegotiateAllQueryToJSONTyped(json, false);
}

export function NegotiateAllQueryToJSONTyped(value?: NegotiateAllQuery | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'connectorEndpoint': value['connectorEndpoint'],
        'participantId': value['participantId'],
        'filter': ((value['filter'] as Array<any>).map(AssetFilterConstraintToJSON)),
        'limit': value['limit'],
        'callbackAddresses': value['callbackAddresses'] == null ? undefined : ((value['callbackAddresses'] as Array<any>).map(CallbackAddressDtoToJSON)),
    };
}

