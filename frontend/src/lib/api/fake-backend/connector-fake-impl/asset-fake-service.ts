/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type Patcher, patchObj} from '@/lib/utils/object-utils';
import {
  type IdAvailabilityResponse,
  type IdResponseDto,
  type UiAsset,
  type UiAssetCreateRequest,
  type UiAssetEditRequest,
  type UiDataSource,
  SortByDirection,
  type AssetsPageResult,
  type AssetsPageRequest,
  AssetsPageSortProperty,
} from '@sovity.de/edc-client';
import {TestAssets} from './data/test-assets';
import {filterListPage} from '../utils/list-page-filter-utils';

let assets: UiAsset[] = [
  TestAssets.full,
  TestAssets.onRequestAsset,
  TestAssets.boring,
  TestAssets.short,
  TestAssets.assetWithCustomProperties,
  ...Array.from({length: 10}).map((_, idx) => TestAssets.dummyAsset(idx)),
];

export const assetsPage = (request: AssetsPageRequest): AssetsPageResult => {
  const {content, pagination} = filterListPage(
    request.searchText,
    request.pagination,
    assets,
    (content, query) =>
      content.filter(
        (asset) =>
          asset.title.toLowerCase().includes(query.toLowerCase()) ||
          asset.description?.toLowerCase().includes(query.toLowerCase()),
      ),
    (content) => {
      request.sortBy?.forEach(({direction, field}) => {
        content.sort((a, b) => {
          const propertyName = (() => {
            switch (field) {
              case AssetsPageSortProperty.Title:
                return 'title';
              case AssetsPageSortProperty.Description:
                return 'description';
            }
          })();
          const aValue = a[propertyName] ?? '';
          const bValue = b[propertyName] ?? '';
          if (aValue < bValue) {
            return direction === SortByDirection.Descending ? 1 : -1;
          } else if (aValue > bValue) {
            return direction === SortByDirection.Descending ? -1 : 1;
          } else {
            return 0;
          }
        });
      });
      return content;
    },
  );

  return {assets: content, pagination};
};

export const assetIdAvailable = (assetId: string): IdAvailabilityResponse => {
  return {
    id: assetId,
    available: !assets.some((it) => it.assetId === assetId),
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
    connectorEndpoint: 'https://my-connector/api/v1/dsp',
    participantId: 'BPNL1234XX.C1234XX',
    isOwnConnector: false,
    creatorOrganizationName: 'My Org',
  });
  return {
    id: asset.id,
    lastUpdatedDate: new Date(),
  };
};

export const editAsset = (
  assetId: string,
  request: UiAssetEditRequest,
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
  request: UiAssetCreateRequest | UiAssetEditRequest,
): Omit<
  UiAsset,
  | 'assetId'
  | 'assetJsonLd'
  | 'connectorEndpoint'
  | 'isOwnConnector'
  | 'creatorOrganizationName'
  | 'participantId'
> {
  const dataSource: UiDataSource | null =
    (request as UiAssetCreateRequest).dataSource ??
    (request as UiAssetEditRequest).dataSourceOverrideOrNull;
  return {
    title: request.title ?? assetId,
    description: request.description,
    descriptionShortText: request.description,
    publisherHomepage: request.publisherHomepage,
    dataSourceAvailability:
      dataSource?.type === 'ON_REQUEST' ? 'ON_REQUEST' : 'LIVE',
    language: request.language,
    onRequestContactEmail: dataSource?.onRequest?.contactEmail,
    onRequestContactEmailSubject:
      dataSource?.onRequest?.contactPreferredEmailSubject,
    licenseUrl: request.licenseUrl,
    version: request.version,
    keywords: request.keywords,
    mediaType: request.mediaType,
    landingPageUrl: request.landingPageUrl,
    dataModel: request.dataModel,
    sovereignLegalName: request.sovereignLegalName,
    dataSampleUrls: request.dataSampleUrls,
    referenceFileUrls: request.referenceFileUrls,
    referenceFilesDescription: request.referenceFilesDescription,
    conditionsForUse: request.conditionsForUse,
    dataUpdateFrequency: request.dataUpdateFrequency,
    temporalCoverageFrom: request.temporalCoverageFrom,
    temporalCoverageToInclusive: request.temporalCoverageToInclusive,
    httpDatasourceHintsProxyMethod:
      dataSource?.httpData?.enableMethodParameterization,
    httpDatasourceHintsProxyPath:
      dataSource?.httpData?.enablePathParameterization,
    httpDatasourceHintsProxyQueryParams:
      dataSource?.httpData?.enableQueryParameterization,
    httpDatasourceHintsProxyBody:
      dataSource?.httpData?.enableBodyParameterization,
    customJsonAsString: '{}',
    customJsonLdAsString: '{}',
    privateCustomJsonAsString: '{}',
    privateCustomJsonLdAsString: '{}',
  };
}

export const deleteAsset = (id: string): IdResponseDto => {
  assets = assets.filter((it) => it.assetId !== id);
  return {id, lastUpdatedDate: new Date()};
};
