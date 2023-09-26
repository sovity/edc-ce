import {UiDataOffer} from '@sovity.de/edc-client';
import {TestAssets} from './data/test-assets';
import {TestPolicies} from './data/test-policies';

let dataOffers: UiDataOffer[] = [
  {
    endpoint: 'http://existing-other-connector/api/dsp',
    participantId: 'existing-other-connector',
    asset: TestAssets.full,
    contractOffers: [
      {
        contractOfferId: 'test-contract-offer-1',
        policy: TestPolicies.connectorRestricted,
      },
      {
        contractOfferId: 'test-contract-offer-2',
        policy: TestPolicies.warnings,
      },
    ],
  },
  {
    endpoint: 'http://existing-other-connector/api/dsp',
    asset: TestAssets.boring,
    participantId: 'existing-other-connector',
    contractOffers: [
      {
        contractOfferId: 'test-contract-offer-3',
        policy: TestPolicies.failedMapping,
      },
    ],
  },
];

export const getCatalogPageDataOffers = (
  connectorEndpoint: string,
): UiDataOffer[] => {
  return dataOffers.filter((it) => it.endpoint === connectorEndpoint);
};
