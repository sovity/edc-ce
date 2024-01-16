import {
  AssetPageToJSON,
  ContractAgreementPageToJSON,
  ContractDefinitionPageToJSON,
  ContractDefinitionRequestFromJSON,
  ContractNegotiationRequestFromJSON,
  DashboardPageToJSON,
  FetchAPI,
  IdResponseDtoToJSON,
  InitiateTransferRequestFromJSON,
  PolicyDefinitionCreateRequestFromJSON,
  PolicyDefinitionPageToJSON,
  TransferHistoryPageToJSON,
  UiAssetCreateRequestFromJSON,
  UiAssetEditMetadataRequestFromJSON,
  UiAssetToJSON,
  UiContractNegotiationToJSON,
  UiDataOfferToJSON,
} from '@sovity.de/edc-client';
import {
  assetPage,
  createAsset,
  deleteAsset,
  editAssetMetadata,
} from './connector-fake-impl/asset-fake-service';
import {getCatalogPageDataOffers} from './connector-fake-impl/catalog-fake-service';
import {
  contractAgreementInitiateTransfer,
  contractAgreementPage,
} from './connector-fake-impl/contract-agreement-fake-service';
import {
  contractDefinitionPage,
  createContractDefinition,
  deleteContractDefinition,
} from './connector-fake-impl/contract-definition-fake-service';
import {
  getContractNegotiation,
  initiateContractNegotiation,
} from './connector-fake-impl/contract-negotiation-fake-service';
import {dashboardPage} from './connector-fake-impl/dashboard-fake-service';
import {
  createPolicyDefinition,
  deletePolicyDefinition,
  policyDefinitionPage,
} from './connector-fake-impl/policy-definition-fake-service';
import {
  transferHistoryPage,
  transferProcessAsset,
} from './connector-fake-impl/transfer-history-fake-service';
import {
  getBody,
  getMethod,
  getQueryParams,
  getUrl,
} from './utils/request-utils';
import {ok} from './utils/response-utils';
import {UrlInterceptor} from './utils/url-interceptor';

export const EDC_FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  const url = getUrl(input, 'http://edc.fake-backend/wrapper/ui/');
  const method = getMethod(init);
  const body = getBody(init);
  const params = getQueryParams(input);

  console.log(
    ...[
      'Fake Backend:',
      method,
      url,
      (params as any)['size'] ? params : null,
      body,
    ].filter((it) => !!it),
  );

  return new UrlInterceptor(url, method)
    .url('pages/dashboard-page')
    .on('GET', () => {
      const page = dashboardPage();
      return ok(DashboardPageToJSON(page));
    })

    .url('pages/asset-page')
    .on('GET', () => {
      const page = assetPage();
      return ok(AssetPageToJSON(page));
    })

    .url('pages/asset-page/assets')
    .on('POST', () => {
      const createRequest = UiAssetCreateRequestFromJSON(body);
      const created = createAsset(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('pages/asset-page/assets/*')
    .on('DELETE', (assetId) => {
      const deleted = deleteAsset(assetId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('pages/asset-page/assets/*/metadata')
    .on('PUT', (assetId) => {
      const editRequest = UiAssetEditMetadataRequestFromJSON(body);
      const created = editAssetMetadata(assetId, editRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('pages/policy-page')
    .on('GET', () => {
      const page = policyDefinitionPage();
      return ok(PolicyDefinitionPageToJSON(page));
    })

    .url('pages/policy-page/policy-definitions')
    .on('POST', () => {
      const createRequest = PolicyDefinitionCreateRequestFromJSON(body);
      const created = createPolicyDefinition(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('pages/policy-page/policy-definitions/*')
    .on('DELETE', (policyDefinitionId) => {
      const deleted = deletePolicyDefinition(policyDefinitionId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('pages/contract-definition-page')
    .on('GET', () => {
      const page = contractDefinitionPage();
      return ok(ContractDefinitionPageToJSON(page));
    })

    .url('pages/contract-definition-page/contract-definitions')
    .on('POST', () => {
      const createRequest = ContractDefinitionRequestFromJSON(body);
      const created = createContractDefinition(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('pages/contract-definition-page/contract-definitions/*')
    .on('DELETE', (contractDefinitionId) => {
      const deleted = deleteContractDefinition(contractDefinitionId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('pages/catalog-page/data-offers')
    .on('GET', () => {
      const connectorEndpoint = params.get('connectorEndpoint')!;
      const dataOffers = getCatalogPageDataOffers(connectorEndpoint);
      return ok(dataOffers.map(UiDataOfferToJSON));
    })

    .url('pages/catalog-page/contract-negotiations')
    .on('POST', () => {
      const createRequest = ContractNegotiationRequestFromJSON(body);
      const contractNegotiation = initiateContractNegotiation(createRequest);
      return ok(UiContractNegotiationToJSON(contractNegotiation));
    })

    .url('pages/catalog-page/contract-negotiations/*')
    .on('GET', (contractNegotiationId) => {
      const contractNegotiation = getContractNegotiation(contractNegotiationId);
      return ok(UiContractNegotiationToJSON(contractNegotiation));
    })

    .url('pages/contract-agreement-page')
    .on('GET', () => {
      const page = contractAgreementPage();
      return ok(ContractAgreementPageToJSON(page));
    })

    .url('pages/contract-agreement-page/transfers')
    .on('POST', () => {
      const transferRequest = InitiateTransferRequestFromJSON(body);
      const created = contractAgreementInitiateTransfer(transferRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('pages/transfer-history-page')
    .on('GET', () => {
      const page = transferHistoryPage();
      return ok(TransferHistoryPageToJSON(page));
    })

    .url('pages/transfer-history-page/transfer-processes/*/asset')
    .on('GET', (transferProcessId) => {
      const asset = transferProcessAsset(transferProcessId);
      return ok(UiAssetToJSON(asset));
    })

    .tryMatch();
};
