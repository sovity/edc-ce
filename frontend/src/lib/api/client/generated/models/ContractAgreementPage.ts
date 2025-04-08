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
import type { ContractAgreementCard } from './ContractAgreementCard';
import {
    ContractAgreementCardFromJSON,
    ContractAgreementCardFromJSONTyped,
    ContractAgreementCardToJSON,
    ContractAgreementCardToJSONTyped,
} from './ContractAgreementCard';

/**
 * Data as required by the UI's Contract Agreement Page
 * @export
 * @interface ContractAgreementPage
 */
export interface ContractAgreementPage {
    /**
     * Contract Agreement Cards
     * @type {Array<ContractAgreementCard>}
     * @memberof ContractAgreementPage
     */
    contractAgreements: Array<ContractAgreementCard>;
}

/**
 * Check if a given object implements the ContractAgreementPage interface.
 */
export function instanceOfContractAgreementPage(value: any): value is ContractAgreementPage {
    if (!('contractAgreements' in value) || value['contractAgreements'] === undefined) return false;
    return true;
}

export function ContractAgreementPageFromJSON(json: any): ContractAgreementPage {
    return ContractAgreementPageFromJSONTyped(json, false);
}

export function ContractAgreementPageFromJSONTyped(json: any, ignoreDiscriminator: boolean): ContractAgreementPage {
    if (json == null) {
        return json;
    }
    return {
        
        'contractAgreements': ((json['contractAgreements'] as Array<any>).map(ContractAgreementCardFromJSON)),
    };
}

export function ContractAgreementPageToJSON(json: any): ContractAgreementPage {
    return ContractAgreementPageToJSONTyped(json, false);
}

export function ContractAgreementPageToJSONTyped(value?: ContractAgreementPage | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'contractAgreements': ((value['contractAgreements'] as Array<any>).map(ContractAgreementCardToJSON)),
    };
}

