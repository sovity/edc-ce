import { InjectionToken } from '@angular/core';

export const BASE_PATH = new InjectionToken<string>('basePath');
export const CONNECTOR_MANAGEMENT_API = new InjectionToken<string>('MANAGEMENT_API');
export const CONNECTOR_CATALOG_API = new InjectionToken<string>('CATALOG_API');
export const COLLECTION_FORMATS = {
    'csv': ',',
    'tsv': '   ',
    'ssv': ' ',
    'pipes': '|'
}
