import {UiDataOffer} from '@sovity.de/edc-client';
import {TestAssets} from './data/test-assets';
import {TestPolicies} from './data/test-policies';

let dataOffers: UiDataOffer[] = [
  {
    endpoint: 'http://existing-other-connector/api/dsp',
    participantId: 'MDSL1234XX.C1234XX',
    asset: TestAssets.full,
    contractOffers: [
      {
        contractOfferId:
          'Zmlyc3QtY2Q=:Zmlyc3QtYXNzZXQtMS4w:MjgzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNi',
        policy: TestPolicies.connectorRestricted,
      },
      {
        contractOfferId:
          'Bmlyf3Qt62Q=:Zmlyc3QtYXNzZXQtMS4w:NigzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNj',
        policy: TestPolicies.warnings,
      },
    ],
  },
  {
    endpoint: 'http://existing-other-connector/api/dsp',
    asset: TestAssets.boring,
    participantId: 'MDSL1234XX.C1234XX',
    contractOffers: [
      {
        contractOfferId: 'test-contract-offer-2',
        policy: TestPolicies.failedMapping,
      },
    ],
  },
  {
    endpoint: 'http://existing-other-connector/api/dsp',
    asset: TestAssets.short,
    participantId: 'MDSL1234XX.C1234XX',
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
