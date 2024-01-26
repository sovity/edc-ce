import {
  AssetPage,
  IdResponseDto,
  UiAsset,
  UiAssetCreateRequest,
  UiAssetEditMetadataRequest,
} from '@sovity.de/edc-client';
import {Patcher, patchObj} from '../../../../utils/object-utils';
import {TestAssets} from './data/test-assets';

let assets: UiAsset[] = [TestAssets.full, TestAssets.boring, TestAssets.short];

export const assetPage = (): AssetPage => {
  return {
    assets,
  };
};

export const getAssetById = (id: string) =>
  assets.find((it) => it.assetId === id);

function patchAsset(assetId: string, patcher: Patcher<UiAsset>): UiAsset {
  assets = assets.map((it) =>
    it.assetId === assetId ? patchObj(it, patcher) : it,
  );
  return getAssetById(assetId)!;
}

export const createAsset = (asset: UiAssetCreateRequest): IdResponseDto => {
  const assetId = asset.id;
  assets.push({
    assetId,
    ...createAssetMetadata(assetId, asset),
    connectorEndpoint: 'https://my-connector/api/dsp',
    participantId: 'MDSL1234XX.C1234XX',
    isOwnConnector: false,
    creatorOrganizationName: 'My Org',
  });
  return {
    id: asset.id,
    lastUpdatedDate: new Date(),
  };
};

export const editAssetMetadata = (
  assetId: string,
  request: UiAssetEditMetadataRequest,
): IdResponseDto => {
  const asset = patchAsset(assetId, () =>
    createAssetMetadata(assetId, request),
  );

  return {
    id: asset.assetId,
    lastUpdatedDate: new Date(),
  };
};

function createAssetMetadata(
  assetId: string,
  request: UiAssetCreateRequest | UiAssetEditMetadataRequest,
): Omit<
  UiAsset,
  | 'assetId'
  | 'assetJsonLd'
  | 'connectorEndpoint'
  | 'isOwnConnector'
  | 'creatorOrganizationName'
  | 'httpDatasourceHintsProxyBody'
  | 'httpDatasourceHintsProxyMethod'
  | 'httpDatasourceHintsProxyPath'
  | 'httpDatasourceHintsProxyQueryParams'
  | 'participantId'
> {
  return {
    title: request.title ?? assetId,
    description: request.description,
    descriptionShortText: request.description,
    publisherHomepage: request.publisherHomepage,
    licenseUrl: request.licenseUrl,
    version: request.version,
    keywords: request.keywords,
    mediaType: request.mediaType,
    landingPageUrl: request.landingPageUrl,
    dataCategory: request.dataCategory,
    dataSubcategory: request.dataSubcategory,
    dataModel: request.dataModel,
    geoReferenceMethod: request.geoReferenceMethod,
    transportMode: request.transportMode,
    additionalProperties: request.additionalProperties,
    privateProperties: request.privateProperties,
    sovereignLegalName: request.sovereignLegalName,
    geoLocation: request.geoLocation,
    nutsLocation: request.nutsLocation,
    dataSampleUrls: request.dataSampleUrls,
    referenceFileUrls: request.referenceFileUrls,
    referenceFilesDescription: request.referenceFilesDescription,
    conditionsForUse: request.conditionsForUse,
    dataUpdateFrequency: request.dataUpdateFrequency,
    temporalCoverageFrom: request.temporalCoverageFrom,
    temporalCoverageToInclusive: request.temporalCoverageToInclusive,
  };
}

export const deleteAsset = (id: string): IdResponseDto => {
  assets = assets.filter((it) => it.assetId !== id);
  return {id, lastUpdatedDate: new Date()};
};
