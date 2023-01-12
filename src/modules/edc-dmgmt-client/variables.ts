import { InjectionToken } from '@angular/core';

export const BASE_PATH = new InjectionToken<string>('basePath');
export const CONNECTOR_ORIGINATOR = new InjectionToken<string>('HOME_CONNECTOR_ORIGINATOR');
export const CONNECTOR_ORIGINATOR_ORGANIZATON = new InjectionToken<string>('CONNECTOR_ORIGINATOR_DESCRIPTION')
export const CONNECTOR_DATAMANAGEMENT_API = new InjectionToken<string>('HOME_CONNECTOR_DATA_API');
export const API_KEY = new InjectionToken<string>('API_KEY')
export const COLLECTION_FORMATS = {
    'csv': ',',
    'tsv': '   ',
    'ssv': ' ',
    'pipes': '|'
}
