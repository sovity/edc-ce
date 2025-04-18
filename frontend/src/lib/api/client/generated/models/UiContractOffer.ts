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
import type { UiPolicy } from './UiPolicy';
import {
    UiPolicyFromJSON,
    UiPolicyFromJSONTyped,
    UiPolicyToJSON,
    UiPolicyToJSONTyped,
} from './UiPolicy';

/**
 * Catalog Data Offer's Contract Offer as required by the UI
 * @export
 * @interface UiContractOffer
 */
export interface UiContractOffer {
    /**
     * Contract Offer ID
     * @type {string}
     * @memberof UiContractOffer
     */
    contractOfferId: string;
    /**
     * Policy
     * @type {UiPolicy}
     * @memberof UiContractOffer
     */
    policy: UiPolicy;
}

/**
 * Check if a given object implements the UiContractOffer interface.
 */
export function instanceOfUiContractOffer(value: any): value is UiContractOffer {
    if (!('contractOfferId' in value) || value['contractOfferId'] === undefined) return false;
    if (!('policy' in value) || value['policy'] === undefined) return false;
    return true;
}

export function UiContractOfferFromJSON(json: any): UiContractOffer {
    return UiContractOfferFromJSONTyped(json, false);
}

export function UiContractOfferFromJSONTyped(json: any, ignoreDiscriminator: boolean): UiContractOffer {
    if (json == null) {
        return json;
    }
    return {
        
        'contractOfferId': json['contractOfferId'],
        'policy': UiPolicyFromJSON(json['policy']),
    };
}

export function UiContractOfferToJSON(json: any): UiContractOffer {
    return UiContractOfferToJSONTyped(json, false);
}

export function UiContractOfferToJSONTyped(value?: UiContractOffer | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'contractOfferId': value['contractOfferId'],
        'policy': UiPolicyToJSON(value['policy']),
    };
}

