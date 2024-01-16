import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  AssetPage,
  ConnectorLimits,
  ContractAgreementPage,
  ContractDefinitionPage,
  ContractDefinitionRequest,
  ContractNegotiationRequest,
  DashboardPage,
  EdcClient,
  IdResponseDto,
  InitiateCustomTransferRequest,
  InitiateTransferRequest,
  PolicyDefinitionCreateRequest,
  PolicyDefinitionPage,
  TransferHistoryPage,
  UiAsset,
  UiAssetCreateRequest,
  UiAssetEditMetadataRequest,
  UiContractNegotiation,
  UiDataOffer,
  buildEdcClient,
} from '@sovity.de/edc-client';
import {APP_CONFIG, AppConfig} from '../../config/app-config';
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
    return from(this.edcClient.uiApi.getDashboardPage());
  }

  getAssetPage(): Observable<AssetPage> {
    return from(this.edcClient.uiApi.getAssetPage());
  }

  createAsset(
    uiAssetCreateRequest: UiAssetCreateRequest,
  ): Observable<IdResponseDto> {
    return from(this.edcClient.uiApi.createAsset({uiAssetCreateRequest}));
  }

  editAssetMetadata(
    assetId: string,
    uiAssetEditMetadataRequest: UiAssetEditMetadataRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.editAssetMetadata({
        assetId,
        uiAssetEditMetadataRequest,
      }),
    );
  }

  deleteAsset(assetId: string): Observable<IdResponseDto> {
    return from(this.edcClient.uiApi.deleteAsset({assetId}));
  }

  getPolicyDefinitionPage(): Observable<PolicyDefinitionPage> {
    return from(this.edcClient.uiApi.getPolicyDefinitionPage());
  }

  createPolicyDefinition(
    policyDefinitionCreateRequest: PolicyDefinitionCreateRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.createPolicyDefinition({
        policyDefinitionCreateRequest,
      }),
    );
  }

  deletePolicyDefinition(policyId: string): Observable<IdResponseDto> {
    return from(this.edcClient.uiApi.deletePolicyDefinition({policyId}));
  }

  getContractDefinitionPage(): Observable<ContractDefinitionPage> {
    return from(this.edcClient.uiApi.getContractDefinitionPage());
  }

  createContractDefinition(
    contractDefinitionRequest: ContractDefinitionRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.createContractDefinition({
        contractDefinitionRequest,
      }),
    );
  }

  deleteContractDefinition(
    contractDefinitionId: string,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.deleteContractDefinition({contractDefinitionId}),
    );
  }

  getCatalogPageDataOffers(
    connectorEndpoint: string,
  ): Observable<UiDataOffer[]> {
    return from(
      this.edcClient.uiApi.getCatalogPageDataOffers({connectorEndpoint}),
    );
  }

  initiateContractNegotiation(
    contractNegotiationRequest: ContractNegotiationRequest,
  ): Observable<UiContractNegotiation> {
    return from(
      this.edcClient.uiApi.initiateContractNegotiation({
        contractNegotiationRequest,
      }),
    );
  }

  getContractNegotiation(
    contractNegotiationId: string,
  ): Observable<UiContractNegotiation> {
    return from(
      this.edcClient.uiApi.getContractNegotiation({contractNegotiationId}),
    );
  }

  getContractAgreementPage(): Observable<ContractAgreementPage> {
    return from(this.edcClient.uiApi.getContractAgreementPage());
  }

  initiateTransfer(
    initiateTransferRequest: InitiateTransferRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.initiateTransfer({initiateTransferRequest}),
    );
  }

  initiateCustomTransfer(
    initiateCustomTransferRequest: InitiateCustomTransferRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.initiateCustomTransfer({
        initiateCustomTransferRequest,
      }),
    );
  }

  getTransferHistoryPage(): Observable<TransferHistoryPage> {
    return from(this.edcClient.uiApi.getTransferHistoryPage());
  }

  getTransferProcessAsset(transferProcessId: string): Observable<UiAsset> {
    return from(
      this.edcClient.uiApi.getTransferProcessAsset({transferProcessId}),
    );
  }

  getEnterpriseEditionConnectorLimits(): Observable<ConnectorLimits> {
    return from(this.edcClient.enterpriseEditionApi.connectorLimits());
  }
}
