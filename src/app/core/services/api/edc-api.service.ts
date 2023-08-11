import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  AssetPage,
  ConnectorLimits,
  ContractAgreementPage,
  ContractAgreementTransferRequest,
  EdcClient,
  IdResponse,
  IdResponseDto,
  buildEdcClient,
} from '@sovity.de/edc-client';
import {AssetCreateRequest} from '@sovity.de/edc-client/dist/generated/models/AssetCreateRequest';
import {APP_CONFIG, AppConfig} from '../../config/app-config';

@Injectable({providedIn: 'root'})
export class EdcApiService {
  edcClient: EdcClient;

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {
    this.edcClient = buildEdcClient({
      managementApiUrl: this.config.managementApiUrl,
      managementApiKey: this.config.managementApiKey,
    });
  }

  createAsset(
    assetCreateRequest: AssetCreateRequest,
  ): Observable<IdResponseDto> {
    return from(this.edcClient.uiApi.createAsset({assetCreateRequest}));
  }

  getAssetPage(): Observable<AssetPage> {
    return from(this.edcClient.uiApi.assetPage());
  }

  deleteAsset(
    assetId: string,
  ): Observable<IdResponseDto> {
    return from(this.edcClient.uiApi.deleteAsset({assetId}));
  }

  getContractAgreementPage(): Observable<ContractAgreementPage> {
    return from(this.edcClient.uiApi.contractAgreementEndpoint());
  }

  initiateTranfer(
    contractAgreementTransferRequest: ContractAgreementTransferRequest,
  ): Observable<IdResponse> {
    return from(
      this.edcClient.uiApi.initiateTransfer({contractAgreementTransferRequest}),
    );
  }

  getEnterpriseEditionConnectorLimits(): Observable<ConnectorLimits> {
    return from(this.edcClient.enterpriseEditionApi.connectorLimits());
  }
}
