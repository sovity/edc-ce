import {AssetDto, AssetEntry} from '@sovity.de/edc-client';
import {AssetProperties} from '../../../asset-properties';

export namespace TestAssets {
  export const boring: AssetEntry = {
    properties: {
      [AssetProperties.id]: 'test-asset-1',
      [AssetProperties.name]: 'Test Asset 1',
      [AssetProperties.description]: 'This is a test asset.',
    },
    privateProperties: {
      'some-private-property': 'abc',
    },
  };

  export const full: AssetEntry = {
    properties: {
      [AssetProperties.id]: 'urn:artifact:my-test-asset-4',
      [AssetProperties.name]: 'Rail Network 2023 NRW - RailDesigner Export',
      [AssetProperties.version]: '1.1',
      [AssetProperties.originatorOrganization]: 'Deutsche Bahn AG',
      [AssetProperties.keywords]: 'db, bahn, rail, Rail-Designer',
      [AssetProperties.contentType]: 'application/json',
      [AssetProperties.description]:
        'Train Network Map released on 10.01.2023, valid until 31.02.2023. \nFile format is xyz as exported by Rail-Designer.',
      [AssetProperties.language]: 'https://w3id.org/idsa/code/EN',
      [AssetProperties.publisher]: 'https://my.cool-api.gg/about',
      [AssetProperties.standardLicense]: 'https://my.cool-api.gg/license',
      [AssetProperties.endpointDocumentation]: 'https://my.cool-api.gg/docs',
      [AssetProperties.dataCategory]: 'Infrastructure and Logistics',
      [AssetProperties.dataSubcategory]:
        'General Information About Planning Of Routes',
      [AssetProperties.dataModel]: 'my-data-model-001',
      [AssetProperties.geoReferenceMethod]: 'my-geo-reference-method',
      [AssetProperties.transportMode]: 'Rail',
      'asset:prop:some-unsupported-property':
        'F10E2821BBBEA527EA02200352313BC059445190',
    },
    privateProperties: {},
  };

  export function toAssetDto(entry: AssetEntry): AssetDto {
    return {
      assetId: entry.properties[AssetProperties.id],
      createdAt: new Date(),
      properties: entry.properties,
    };
  }

  export function toDummyAsset(entry: AssetEntry): AssetEntry {
    return dummyAsset(entry.properties[AssetProperties.id]);
  }

  export function dummyAsset(assetId: string): AssetEntry {
    return {
      properties: {
        [AssetProperties.id]: assetId,
      },
      privateProperties: {},
    };
  }
}
