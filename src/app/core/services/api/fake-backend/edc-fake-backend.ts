import {
  AssetCreateRequest,
  ContractDefinitionRequest,
  FetchAPI,
  PolicyDefinitionCreateRequest,
} from '@sovity.de/edc-client';
import {assetPage, createAsset, deleteAsset} from './asset-fake-service';
import {
  contractDefinitionPage,
  createContractDefinition,
  deleteContractDefinition,
} from './contract-definition-fake-service';
import {
  createPolicyDefinition,
  deletePolicyDefinition,
  policyDefinitionPage,
} from './policy-definition-fake-service';
import {getBody, getMethod, getUrl} from './utils/request-utils';
import {ok} from './utils/response-utils';
import {UrlInterceptor} from './utils/url-interceptor';

export const EDC_FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  let url = getUrl(input, 'http://edc.fake-backend/wrapper/ui/');
  let method = getMethod(init);
  let body = getBody(init);

  console.log(...['Fake Backend:', method, url, body].filter((it) => !!it));

  return new UrlInterceptor(url, method)

    .url('pages/asset-page')
    .on('GET', () => ok(assetPage()))

    .url('pages/asset-page/assets')
    .on('POST', () => ok(createAsset(body as AssetCreateRequest)))

    .url('pages/asset-page/assets/*')
    .on('DELETE', (assetId) => ok(deleteAsset(assetId)))

    .url('pages/contract-definition-page')
    .on('GET', () => ok(contractDefinitionPage()))

    .url('pages/contract-definition-page/contract-definitions')
    .on('POST', () =>
      ok(createContractDefinition(body as ContractDefinitionRequest)),
    )

    .url('pages/contract-definition-page/contract-definitions/*')
    .on('DELETE', (contractDefinitionId) =>
      ok(deleteContractDefinition(contractDefinitionId)),
    )

    .url('pages/policy-page')
    .on('GET', () => ok(policyDefinitionPage()))

    .url('pages/policy-page/policy-definitions')
    .on('POST', () =>
      ok(createPolicyDefinition(body as PolicyDefinitionCreateRequest)),
    )

    .url('pages/policy-page/policy-definitions/*')
    .on('DELETE', (policyDefinitionId) =>
      ok(deletePolicyDefinition(policyDefinitionId)),
    )

    .tryMatch();
};
