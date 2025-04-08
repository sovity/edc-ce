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
 * Enabled UI Features
 * @export
 */
export const UiConfigFeature = {
    ConnectorLimits: 'CONNECTOR_LIMITS',
    OpenSourceMarketing: 'OPEN_SOURCE_MARKETING',
    EeBasicMarketing: 'EE_BASIC_MARKETING'
} as const;
export type UiConfigFeature = typeof UiConfigFeature[keyof typeof UiConfigFeature];


export function instanceOfUiConfigFeature(value: any): boolean {
    for (const key in UiConfigFeature) {
        if (Object.prototype.hasOwnProperty.call(UiConfigFeature, key)) {
            if (UiConfigFeature[key as keyof typeof UiConfigFeature] === value) {
                return true;
            }
        }
    }
    return false;
}

export function UiConfigFeatureFromJSON(json: any): UiConfigFeature {
    return UiConfigFeatureFromJSONTyped(json, false);
}

export function UiConfigFeatureFromJSONTyped(json: any, ignoreDiscriminator: boolean): UiConfigFeature {
    return json as UiConfigFeature;
}

export function UiConfigFeatureToJSON(value?: UiConfigFeature | null): any {
    return value as any;
}

export function UiConfigFeatureToJSONTyped(value: any, ignoreDiscriminator: boolean): UiConfigFeature {
    return value as UiConfigFeature;
}

