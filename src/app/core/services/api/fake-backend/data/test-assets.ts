import {UiAsset} from '@sovity.de/edc-client';

export namespace TestAssets {
  export const boring: UiAsset = {
    assetId: 'test-asset-1',
    name: 'Test Asset 1',
    description: 'This is a test asset.',
    privateProperties: {
      'some-private-property': 'abc',
    },
  };

  export const full: UiAsset = {
    assetId: 'urn:artifact:my-test-asset-4',
    name: 'Rail Network 2023 NRW - RailDesigner Export',
    version: '1.1',
    creatorOrganizationName: 'Deutsche Bahn AG',
    keywords: ['db', 'bahn', 'rail', 'Rail-Designer'],
    mediaType: 'application/json',
    description:
      'Train Network Map released on 10.01.2023, valid until 31.02.2023. \nFile format is xyz as exported by Rail-Designer.',
    language: 'https://w3id.org/idsa/code/EN',
    publisherHomepage: 'https://my.cool-api.gg/about',
    licenseUrl: 'https://my.cool-api.gg/license',
    landingPageUrl: 'https://my.cool-api.gg/docs',
    dataCategory: 'Infrastructure and Logistics',
    dataSubcategory: 'General Information About Planning Of Routes',
    dataModel: 'my-data-model-001',
    geoReferenceMethod: 'my-geo-reference-method',
    transportMode: 'Rail',
    httpDatasourceHintsProxyQueryParams: true,
    httpDatasourceHintsProxyPath: false,
    httpDatasourceHintsProxyMethod: false,
    httpDatasourceHintsProxyBody: false,
    additionalProperties: {
      'asset:prop:some-unsupported-property':
        'F10E2821BBBEA527EA02200352313BC059445190',
    },
    privateProperties: {},
  };

  export function toAssetDto(entry: UiAsset): UiAsset {
    return {
      assetId: entry.assetId,
      name: entry.name,
      additionalProperties: {},
    };
  }

  export function toDummyAsset(entry: UiAsset): UiAsset {
    return dummyAsset(entry.assetId);
  }

  export function dummyAsset(assetId: string): UiAsset {
    return {
      assetId,
      name: 'Dummy Asset',
      privateProperties: {},
    };
  }
}
