/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {uiConfig} from '@/lib/api/fake-backend/connector-fake-impl/ui-config-fake-service';
import {
  AssetPageToJSON,
  BuildInfoToJSON,
  ConnectorLimitsToJSON,
  ContractAgreementPageQueryFromJSON,
  ContractAgreementPageToJSON,
  ContractDefinitionPageToJSON,
  ContractDefinitionRequestFromJSON,
  ContractNegotiationRequestFromJSON,
  ContractTerminationRequestFromJSON,
  DashboardPageToJSON,
  DataOfferCreateRequestFromJSON,
  type FetchAPI,
  IdAvailabilityResponseToJSON,
  IdResponseDtoToJSON,
  InitiateTransferRequestFromJSON,
  PolicyDefinitionCreateDtoFromJSON,
  PolicyDefinitionCreateRequestFromJSON,
  PolicyDefinitionPageToJSON,
  TransferHistoryPageToJSON,
  UiAssetCreateRequestFromJSON,
  UiAssetEditRequestFromJSON,
  UiAssetToJSON,
  UiConfigToJSON,
  UiContractNegotiationToJSON,
  UiDataOfferToJSON,
} from '@sovity.de/edc-client';
import {
  assetIdAvailable,
  assetPage,
  createAsset,
  deleteAsset,
  editAsset,
} from './connector-fake-impl/asset-fake-service';
import {buildInfo} from './connector-fake-impl/buildinfo-fake-service';
import {getCatalogPageDataOffers} from './connector-fake-impl/catalog-fake-service';
import {
  contractAgreementInitiateTransfer,
  contractAgreementPage,
} from './connector-fake-impl/contract-agreement-fake-service';
import {
  contractDefinitionIdAvailable,
  contractDefinitionPage,
  createContractDefinition,
  deleteContractDefinition,
} from './connector-fake-impl/contract-definition-fake-service';
import {
  getContractNegotiation,
  initiateContractNegotiation,
} from './connector-fake-impl/contract-negotiation-fake-service';
import {initiateContractTermination} from './connector-fake-impl/contract-termination-fake-service';
import {dashboardPage} from './connector-fake-impl/dashboard-fake-service';
import {createDataOffer} from './connector-fake-impl/data-offer-fake-service';
import {connectorLimits} from './connector-fake-impl/ee-fake-service';
import {
  createPolicyDefinition,
  createPolicyDefinitionV2,
  deletePolicyDefinition,
  policyDefinitionIdAvailable,
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
  const url = getUrl(input, 'http://edc.fake-backend/wrapper/');
  const method = getMethod(init);
  const body: unknown = getBody(init);
  const params = getQueryParams(input);

  console.log(
    ...[
      'Fake Backend:',
      method,
      url,
      params?.get('size') ? params : null,
      body,
    ].filter((it) => !!it),
  );

  return new UrlInterceptor(url, method)
    .url('ui/pages/dashboard-page')
    .on('GET', () => {
      const page = dashboardPage();
      return ok(DashboardPageToJSON(page));
    })

    .url('ui/pages/asset-page')
    .on('GET', () => {
      const page = assetPage();
      return ok(AssetPageToJSON(page));
    })

    .url('ui/pages/asset-page/assets')
    .on('POST', () => {
      const createRequest = UiAssetCreateRequestFromJSON(body);
      const created = createAsset(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/pages/asset-page/assets/*')
    .on('DELETE', (assetId) => {
      const deleted = deleteAsset(assetId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('ui/pages/asset-page/assets/*')
    .on('PUT', (assetId) => {
      const editRequest = UiAssetEditRequestFromJSON(body);
      const created = editAsset(assetId, editRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/pages/policy-page')
    .on('GET', () => {
      const page = policyDefinitionPage();
      return ok(PolicyDefinitionPageToJSON(page));
    })

    .url('ui/pages/policy-page/policy-definitions')
    .on('POST', () => {
      const createRequest = PolicyDefinitionCreateRequestFromJSON(body);
      const created = createPolicyDefinition(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/v2/pages/policy-page/policy-definitions')
    .on('POST', () => {
      const createRequest = PolicyDefinitionCreateDtoFromJSON(body);
      const created = createPolicyDefinitionV2(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/pages/policy-page/policy-definitions/*')
    .on('DELETE', (policyDefinitionId) => {
      const deleted = deletePolicyDefinition(policyDefinitionId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('ui/pages/contract-definition-page')
    .on('GET', () => {
      const page = contractDefinitionPage();
      return ok(ContractDefinitionPageToJSON(page));
    })

    .url('ui/pages/contract-definition-page/contract-definitions')
    .on('POST', () => {
      const createRequest = ContractDefinitionRequestFromJSON(body);
      const created = createContractDefinition(createRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/pages/contract-definition-page/contract-definitions/*')
    .on('DELETE', (contractDefinitionId) => {
      const deleted = deleteContractDefinition(contractDefinitionId);
      return ok(IdResponseDtoToJSON(deleted));
    })

    .url('ui/pages/catalog-page/data-offers')
    .on('GET', () => {
      const connectorEndpoint = params.get('connectorEndpoint')!;
      const dataOffers = getCatalogPageDataOffers(connectorEndpoint);
      return ok(dataOffers.map(UiDataOfferToJSON));
    })

    .url('ui/pages/catalog-page/contract-negotiations')
    .on('POST', () => {
      const createRequest = ContractNegotiationRequestFromJSON(body);
      const contractNegotiation = initiateContractNegotiation(createRequest);
      return ok(UiContractNegotiationToJSON(contractNegotiation));
    })

    .url('ui/pages/catalog-page/contract-negotiations/*')
    .on('GET', (contractNegotiationId) => {
      const contractNegotiation = getContractNegotiation(contractNegotiationId);
      return ok(UiContractNegotiationToJSON(contractNegotiation));
    })

    .url('ui/pages/contract-agreement-page')
    .on('POST', () => {
      const pageQuery = body ? ContractAgreementPageQueryFromJSON(body) : null;
      const page = contractAgreementPage(pageQuery?.terminationStatus);
      return ok(ContractAgreementPageToJSON(page));
    })

    .url('ui/pages/contract-agreement-page/*')
    .on('GET', (contractAgreementId: string) => {
      return ok(
        contractAgreementPage().contractAgreements.find(
          (contractAgreement) =>
            contractAgreement.contractAgreementId === contractAgreementId,
        ),
      );
    })

    .url('ui/pages/content-agreement-page/*/terminate')
    .on('POST', (contractAgreementId) => {
      const request = ContractTerminationRequestFromJSON(body);
      const response = initiateContractTermination({
        contractAgreementId: contractAgreementId,
        contractTerminationRequest: request,
      });
      return ok(IdResponseDtoToJSON(response));
    })

    .url('ui/pages/contract-agreement-page/transfers')
    .on('POST', () => {
      const transferRequest = InitiateTransferRequestFromJSON(body);
      const created = contractAgreementInitiateTransfer(transferRequest);
      return ok(IdResponseDtoToJSON(created));
    })

    .url('ui/pages/transfer-history-page')
    .on('GET', () => {
      const page = transferHistoryPage();
      return ok(TransferHistoryPageToJSON(page));
    })

    .url('ui/pages/transfer-history-page/transfer-processes/*/asset')
    .on('GET', (transferProcessId) => {
      const asset = transferProcessAsset(transferProcessId);
      return ok(UiAssetToJSON(asset));
    })

    .url('ee/connector-limits')
    .on('GET', () => {
      const limits = connectorLimits();
      return ok(ConnectorLimitsToJSON(limits));
    })

    .url('ui/pages/create-data-offer')
    .on('POST', () => {
      const response = createDataOffer(DataOfferCreateRequestFromJSON(body));
      return ok(IdResponseDtoToJSON(response));
    })

    .url('ui/pages/data-offer-page/validate-asset-id/*')
    .on('GET', (assetId) => {
      const response = assetIdAvailable(assetId);
      return ok(IdAvailabilityResponseToJSON(response));
    })

    .url('ui/pages/data-offer-page/validate-policy-id/*')
    .on('GET', (policyId) => {
      const response = policyDefinitionIdAvailable(policyId);
      return ok(IdAvailabilityResponseToJSON(response));
    })

    .url('ui/pages/data-offer-page/validate-contract-definition-id/*')
    .on('GET', (contractDefinitionId) => {
      const response = contractDefinitionIdAvailable(contractDefinitionId);
      return ok(IdAvailabilityResponseToJSON(response));
    })

    .url('ui/build-info')
    .on('GET', () => {
      const response = buildInfo();
      return ok(BuildInfoToJSON(response));
    })

    .url('ui/config')
    .on('GET', () => {
      const response = uiConfig();
      return ok(UiConfigToJSON(response));
    })

    .tryMatch();
};
