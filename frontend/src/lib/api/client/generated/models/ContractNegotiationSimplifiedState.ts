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
 * Simplified Contract Negotiation State to be used in UI
 * @export
 */
export const ContractNegotiationSimplifiedState = {
    InProgress: 'IN_PROGRESS',
    Agreed: 'AGREED',
    Terminated: 'TERMINATED'
} as const;
export type ContractNegotiationSimplifiedState = typeof ContractNegotiationSimplifiedState[keyof typeof ContractNegotiationSimplifiedState];


export function instanceOfContractNegotiationSimplifiedState(value: any): boolean {
    for (const key in ContractNegotiationSimplifiedState) {
        if (Object.prototype.hasOwnProperty.call(ContractNegotiationSimplifiedState, key)) {
            if (ContractNegotiationSimplifiedState[key as keyof typeof ContractNegotiationSimplifiedState] === value) {
                return true;
            }
        }
    }
    return false;
}

export function ContractNegotiationSimplifiedStateFromJSON(json: any): ContractNegotiationSimplifiedState {
    return ContractNegotiationSimplifiedStateFromJSONTyped(json, false);
}

export function ContractNegotiationSimplifiedStateFromJSONTyped(json: any, ignoreDiscriminator: boolean): ContractNegotiationSimplifiedState {
    return json as ContractNegotiationSimplifiedState;
}

export function ContractNegotiationSimplifiedStateToJSON(value?: ContractNegotiationSimplifiedState | null): any {
    return value as any;
}

export function ContractNegotiationSimplifiedStateToJSONTyped(value: any, ignoreDiscriminator: boolean): ContractNegotiationSimplifiedState {
    return value as ContractNegotiationSimplifiedState;
}

