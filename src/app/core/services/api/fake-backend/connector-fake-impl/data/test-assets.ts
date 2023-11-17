import {UiAsset} from '@sovity.de/edc-client';

export namespace TestAssets {
  export const boring: UiAsset = {
    assetId: 'data-sample-ckd-skd-demands-2023-Jan',
    title: 'data-sample-ckd-skd-demands-2023-Jan',
    connectorEndpoint: 'https://my-other-connector/api/dsp',
    participantId: 'my-other-connector',
    creatorOrganizationName: 'my-other-connector',
  };

  export const full: UiAsset = {
    assetId: 'ckd-skd-demands-2023-Jan',
    title: 'CKD / SKD Demands January 2023',
    connectorEndpoint: 'https://my-other-connector/api/dsp',
    participantId: 'my-other-connector',
    version: '2023-A-Program',
    creatorOrganizationName: 'My-German-OEM',
    keywords: ['automotive', 'part-demands', '2023', 'January'],
    mediaType: 'application/json',
    description:
      'Part demands for CKD/SKD parts for January 2023. Split by plant / day / model code.',
    language: 'https://w3id.org/idsa/code/EN',
    publisherHomepage:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands',
    licenseUrl:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands#license',
    landingPageUrl:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands#documentation',
    dataCategory: 'Infrastructure and Logistics',
    dataSubcategory: 'General Information About Planning Of Routes',
    dataModel: 'unspecified',
    geoReferenceMethod: 'Lat/Lon',
    transportMode: 'Rail',
    httpDatasourceHintsProxyQueryParams: true,
    httpDatasourceHintsProxyPath: true,
    httpDatasourceHintsProxyMethod: true,
    httpDatasourceHintsProxyBody: true,
    additionalProperties: {
      'http://unknown/usecase': 'my-use-case',
    },
    privateProperties: {
      'http://unknown/internal-id': 'my-internal-id-123',
    },
  };

  export function toDummyAsset(entry: UiAsset): UiAsset {
    return {
      assetId: entry.assetId,
      title: entry.assetId,
      participantId: entry.participantId,
      connectorEndpoint: entry.connectorEndpoint,
      creatorOrganizationName: entry.participantId,
    };
  }

  export function withSuffix(asset: UiAsset, suffix: string): UiAsset {
    return {
      ...asset,
      assetId: `${asset.assetId}-${suffix}`,
      title: `${asset.title} ${suffix}`,
    };
  }
}
