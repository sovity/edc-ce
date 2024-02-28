import {
  CatalogContractOffer,
  CatalogDataOffer,
  CatalogPageQuery,
  CatalogPageResult,
  DataOfferDetailContractOffer,
  DataOfferDetailPageQuery,
  DataOfferDetailPageResult,
  UiAsset,
} from '@sovity.de/broker-server-client';
import {subDays, subMinutes} from 'date-fns';
import {TestAssets} from '../connector-fake-impl/data/test-assets';
import {TestPolicies} from '../connector-fake-impl/data/test-policies';

const DATA_OFFERS: DataOfferDetailPageResult[] = [
  {
    assetId: TestAssets.full.assetId,
    asset: TestAssets.full as UiAsset,
    connectorEndpoint: 'https://example-connector/api/dsp',
    viewCount: 103,
    connectorOfflineSinceOrLastUpdatedAt: subMinutes(new Date(), 5),
    updatedAt: subMinutes(new Date(), 5),
    createdAt: subDays(new Date(), 7),
    connectorOnlineStatus: 'ONLINE',
    contractOffers: [
      {
        contractOfferId: 'contract-offer-1',
        updatedAt: subMinutes(new Date(), 5),
        createdAt: subDays(new Date(), 7),
        contractPolicy: TestPolicies.warnings,
      },
    ],
  },
  {
    assetId: TestAssets.withSuffix(TestAssets.boring, '2').assetId,
    asset: TestAssets.withSuffix(TestAssets.boring, '2') as UiAsset,
    connectorEndpoint: 'https://example-connector/api/dsp',
    viewCount: 103,
    connectorOfflineSinceOrLastUpdatedAt: subMinutes(new Date(), 5),
    updatedAt: subMinutes(new Date(), 5),
    createdAt: subDays(new Date(), 7),
    connectorOnlineStatus: 'OFFLINE',
    contractOffers: [
      {
        contractOfferId: 'contract-offer-1',
        updatedAt: subMinutes(new Date(), 5),
        createdAt: subDays(new Date(), 7),
        contractPolicy: TestPolicies.warnings,
      },
    ],
  },
  {
    assetId: TestAssets.boring.assetId,
    asset: TestAssets.boring as UiAsset,
    connectorEndpoint: 'https://example-connector/api/dsp',
    viewCount: 103,
    connectorOfflineSinceOrLastUpdatedAt: subDays(new Date(), 3),
    updatedAt: subMinutes(new Date(), 5),
    createdAt: subDays(new Date(), 7),
    connectorOnlineStatus: 'DEAD',
    contractOffers: [
      {
        contractOfferId: 'contract-offer-1',
        updatedAt: subMinutes(new Date(), 5),
        createdAt: subDays(new Date(), 7),
        contractPolicy: TestPolicies.warnings,
      },
    ],
  },
  {
    assetId: TestAssets.short.assetId,
    asset: TestAssets.short as UiAsset,
    connectorEndpoint: 'https://example-connector/api/dsp',
    viewCount: 33,
    connectorOfflineSinceOrLastUpdatedAt: subDays(new Date(), 3),
    updatedAt: subMinutes(new Date(), 5),
    createdAt: subDays(new Date(), 7),
    connectorOnlineStatus: 'DEAD',
    contractOffers: [
      {
        contractOfferId: 'contract-offer-1',
        updatedAt: subMinutes(new Date(), 5),
        createdAt: subDays(new Date(), 7),
        contractPolicy: TestPolicies.warnings,
      },
    ],
  },
];

export const getCatalogPage = (query: CatalogPageQuery): CatalogPageResult => {
  const dataOffers: CatalogDataOffer[] = DATA_OFFERS.map(buildCatalogDataOffer);

  return {
    dataOffers,
    availableFilters: {
      fields: [
        {
          id: 'example-filter',
          title: 'Example Filter',
          values: [
            {id: 'example-value', title: 'Example Value'},
            {id: 'other-value', title: 'Other Value'},
            {id: '', title: ''},
          ],
        },
        {
          id: 'other-filter',
          title: 'Other Filter',
          values: [
            {id: 'example-value', title: 'Example Value'},
            {id: 'other-value', title: 'Other Value'},
            {id: '', title: ''},
          ],
        },
        {
          id: 'connectorEndpoint',
          title: 'Connector',
          values: [
            {
              id: 'https://example-connector/api/dsp',
              title: 'https://example-connector/api/dsp',
            },
            {
              id: 'https://example-connector2/api/dsp',
              title: 'https://example-connector2/api/dsp',
            },
          ],
        },
      ],
    },
    paginationMetadata: {
      pageSize: 20,
      numVisible: dataOffers.length,
      pageOneBased: 0,
      numTotal: dataOffers.length,
    },
    availableSortings: [
      {sorting: 'TITLE', title: 'Test Sorting'},
      {sorting: 'MOST_RECENT', title: 'Other Sorting'},
    ],
  };
};

export const getDataOfferDetailPage = (
  query: DataOfferDetailPageQuery,
): DataOfferDetailPageResult => {
  return DATA_OFFERS.find(
    (it) =>
      it.connectorEndpoint === query.connectorEndpoint &&
      it.assetId === query.assetId,
  )!;
};

const buildCatalogDataOffer = (
  it: DataOfferDetailPageResult,
): CatalogDataOffer => ({
  assetId: it.assetId,
  connectorEndpoint: it.connectorEndpoint,
  asset: it.asset,
  contractOffers: it.contractOffers.map(buildCatalogContractOffer),
  updatedAt: it.updatedAt,
  createdAt: it.createdAt,
  connectorOfflineSinceOrLastUpdatedAt: it.connectorOfflineSinceOrLastUpdatedAt,
  connectorOnlineStatus: it.connectorOnlineStatus,
});

const buildCatalogContractOffer = (
  co: DataOfferDetailContractOffer,
): CatalogContractOffer => ({
  contractOfferId: co.contractOfferId,
  contractPolicy: co.contractPolicy,
  createdAt: co.createdAt,
  updatedAt: co.updatedAt,
});
