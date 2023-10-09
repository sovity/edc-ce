import {
  AssetPage,
  IdResponseDto,
  UiAsset,
  UiAssetCreateRequest,
} from '@sovity.de/edc-client';
import {TestAssets} from './data/test-assets';

let assets: UiAsset[] = [TestAssets.full, TestAssets.boring];

export const assetPage = (): AssetPage => {
  return {
    assets,
  };
};

export const getAssetById = (id: string) =>
  assets.find((it) => it.assetId === id);

export const createAsset = (asset: UiAssetCreateRequest): IdResponseDto => {
  assets.push({
    assetId: asset.id,
    title: asset.title ?? asset.id,
    connectorEndpoint: 'https://my-connector/api/dsp',
    participantId: 'my-connector',
    description: asset.description,
    creatorOrganizationName: 'My Org',
    publisherHomepage: asset.publisherHomepage,
    licenseUrl: asset.licenseUrl,
    version: asset.version,
    keywords: asset.keywords,
    mediaType: asset.mediaType,
    landingPageUrl: asset.landingPageUrl,
    dataCategory: asset.dataCategory,
    dataSubcategory: asset.dataSubcategory,
    dataModel: asset.dataModel,
    geoReferenceMethod: asset.geoReferenceMethod,
    transportMode: asset.transportMode,
    additionalProperties: asset.additionalProperties,
    privateProperties: asset.privateProperties,
  });
  return {
    id: asset.id,
    lastUpdatedDate: new Date(),
  };
};

export const deleteAsset = (id: string): IdResponseDto => {
  assets = assets.filter((it) => it.assetId !== id);
  return {id, lastUpdatedDate: new Date()};
};
