import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {
  AssetPage,
  ConnectorLimits,
  ContractAgreementCard,
  ContractAgreementPage,
  ContractDefinitionPage,
  ContractDefinitionRequest,
  ContractNegotiationRequest,
  DashboardPage,
  EdcClient,
  GetContractAgreementPageRequest,
  IdAvailabilityResponse,
  IdResponseDto,
  InitiateCustomTransferRequest,
  InitiateTransferRequest,
  PolicyDefinitionCreateDto,
  PolicyDefinitionCreateRequest,
  PolicyDefinitionPage,
  TerminateContractAgreementRequest,
  TransferHistoryPage,
  UiAsset,
  UiAssetCreateRequest,
  UiAssetEditRequest,
  UiContractNegotiation,
  UiDataOffer,
  buildEdcClient,
} from '@sovity.de/edc-client';
import {APP_CONFIG, AppConfig} from '../../config/app-config';
import {toObservable} from '../../utils/rxjs-utils';
import {EDC_FAKE_BACKEND} from './fake-backend/edc-fake-backend';

@Injectable({providedIn: 'root'})
export class EdcApiService {
  edcClient: EdcClient;

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {
    this.edcClient = buildEdcClient({
      managementApiUrl: this.config.managementApiUrl,
      managementApiKey: this.config.managementApiKey,
      configOverrides: {
        fetchApi: config.useFakeBackend ? EDC_FAKE_BACKEND : undefined,
      },
    });
  }

  getDashboardPage(): Observable<DashboardPage> {
    return toObservable(() => this.edcClient.uiApi.getDashboardPage());
  }

  getAssetPage(): Observable<AssetPage> {
    return toObservable(() => this.edcClient.uiApi.getAssetPage());
  }

  createAsset(
    uiAssetCreateRequest: UiAssetCreateRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.createAsset({uiAssetCreateRequest}),
    );
  }

  editAsset(
    assetId: string,
    uiAssetEditRequest: UiAssetEditRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.editAsset({
        assetId,
        uiAssetEditRequest,
      }),
    );
  }

  deleteAsset(assetId: string): Observable<IdResponseDto> {
    return toObservable(() => this.edcClient.uiApi.deleteAsset({assetId}));
  }

  getPolicyDefinitionPage(): Observable<PolicyDefinitionPage> {
    return toObservable(() => this.edcClient.uiApi.getPolicyDefinitionPage());
  }

  createPolicyDefinition(
    policyDefinitionCreateRequest: PolicyDefinitionCreateRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.createPolicyDefinition({
        policyDefinitionCreateRequest,
      }),
    );
  }

  createPolicyDefinitionV2(
    policyDefinitionCreateDto: PolicyDefinitionCreateDto,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.createPolicyDefinitionV2({
        policyDefinitionCreateDto,
      }),
    );
  }

  deletePolicyDefinition(policyId: string): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.deletePolicyDefinition({policyId}),
    );
  }

  getContractDefinitionPage(): Observable<ContractDefinitionPage> {
    return toObservable(() => this.edcClient.uiApi.getContractDefinitionPage());
  }

  createContractDefinition(
    contractDefinitionRequest: ContractDefinitionRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.createContractDefinition({
        contractDefinitionRequest,
      }),
    );
  }

  deleteContractDefinition(
    contractDefinitionId: string,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.deleteContractDefinition({contractDefinitionId}),
    );
  }

  getCatalogPageDataOffers(
    connectorEndpoint: string,
  ): Observable<UiDataOffer[]> {
    return toObservable(() =>
      this.edcClient.uiApi.getCatalogPageDataOffers({connectorEndpoint}),
    );
  }

  initiateContractNegotiation(
    contractNegotiationRequest: ContractNegotiationRequest,
  ): Observable<UiContractNegotiation> {
    return toObservable(() =>
      this.edcClient.uiApi.initiateContractNegotiation({
        contractNegotiationRequest,
      }),
    );
  }

  terminateContractAgreement(
    terminateContractAgreementRequest: TerminateContractAgreementRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.terminateContractAgreement(
        terminateContractAgreementRequest,
      ),
    );
  }

  getContractNegotiation(
    contractNegotiationId: string,
  ): Observable<UiContractNegotiation> {
    return toObservable(() =>
      this.edcClient.uiApi.getContractNegotiation({contractNegotiationId}),
    );
  }

  getContractAgreementPage(
    getContractAgreementPageRequest: GetContractAgreementPageRequest,
  ): Observable<ContractAgreementPage> {
    return toObservable(() =>
      this.edcClient.uiApi.getContractAgreementPage(
        getContractAgreementPageRequest,
      ),
    );
  }

  getContractAgreementById(
    contractAgreementId: string,
  ): Observable<ContractAgreementCard> {
    return toObservable(() =>
      this.edcClient.uiApi.getContractAgreementCard({contractAgreementId}),
    );
  }

  initiateTransfer(
    initiateTransferRequest: InitiateTransferRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.initiateTransfer({initiateTransferRequest}),
    );
  }

  initiateCustomTransfer(
    initiateCustomTransferRequest: InitiateCustomTransferRequest,
  ): Observable<IdResponseDto> {
    return toObservable(() =>
      this.edcClient.uiApi.initiateCustomTransfer({
        initiateCustomTransferRequest,
      }),
    );
  }

  getTransferHistoryPage(): Observable<TransferHistoryPage> {
    return toObservable(() => this.edcClient.uiApi.getTransferHistoryPage());
  }

  getTransferProcessAsset(transferProcessId: string): Observable<UiAsset> {
    return toObservable(() =>
      this.edcClient.uiApi.getTransferProcessAsset({transferProcessId}),
    );
  }

  getEnterpriseEditionConnectorLimits(): Observable<ConnectorLimits> {
    return toObservable(() =>
      this.edcClient.enterpriseEditionApi.connectorLimits(),
    );
  }

  isAssetIdAvailable(assetId: string): Observable<IdAvailabilityResponse> {
    return toObservable(() =>
      this.edcClient.uiApi.isAssetIdAvailable({assetId}),
    );
  }

  isPolicyIdAvailable(policyId: string): Observable<IdAvailabilityResponse> {
    return toObservable(() =>
      this.edcClient.uiApi.isPolicyIdAvailable({policyId}),
    );
  }

  isContractDefinitionIdAvailable(
    contractDefinitionId: string,
  ): Observable<IdAvailabilityResponse> {
    return toObservable(() =>
      this.edcClient.uiApi.isContractDefinitionIdAvailable({
        contractDefinitionId,
      }),
    );
  }
}
