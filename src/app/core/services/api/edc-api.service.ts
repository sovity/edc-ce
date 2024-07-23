import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {map} from 'rxjs/operators';
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
import {throwIfNull} from '../../utils/rxjs-utils';
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

  editAsset(
    assetId: string,
    uiAssetEditRequest: UiAssetEditRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.editAsset({
        assetId,
        uiAssetEditRequest,
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

  createPolicyDefinitionV2(
    policyDefinitionCreateDto: PolicyDefinitionCreateDto,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.createPolicyDefinitionV2({
        policyDefinitionCreateDto,
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

  terminateContractAgreement(
    terminateContractAgreementRequest: TerminateContractAgreementRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.terminateContractAgreement(
        terminateContractAgreementRequest,
      ),
    );
  }

  getContractNegotiation(
    contractNegotiationId: string,
  ): Observable<UiContractNegotiation> {
    return from(
      this.edcClient.uiApi.getContractNegotiation({contractNegotiationId}),
    );
  }

  getContractAgreementPage(
    getContractAgreementPageRequest: GetContractAgreementPageRequest,
  ): Observable<ContractAgreementPage> {
    return from(
      this.edcClient.uiApi.getContractAgreementPage(
        getContractAgreementPageRequest,
      ),
    );
  }

  getContractAgreementById(
    contractAgreementId: string,
  ): Observable<ContractAgreementCard> {
    return this.getContractAgreementPage({
      contractAgreementPageQuery: {terminationStatus: undefined},
    }).pipe(
      map((page) =>
        page.contractAgreements.find(
          (it) => it.contractAgreementId === contractAgreementId,
        ),
      ),
      throwIfNull(
        `Contract Agreement with ID ${contractAgreementId} not found`,
      ),
    );
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
