import {Inject, Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  ConnectorLimits,
  ContractAgreementPage,
  ContractAgreementTransferRequest,
  EdcClient,
  IdResponseDto,
  buildEdcClient,
} from '@sovity.de/edc-client';
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

  getContractAgreementPage(): Observable<ContractAgreementPage> {
    return from(this.edcClient.uiApi.contractAgreementEndpoint());
  }

  initiateTranfer(
    contractAgreementTransferRequest: ContractAgreementTransferRequest,
  ): Observable<IdResponseDto> {
    return from(
      this.edcClient.uiApi.initiateTransfer({contractAgreementTransferRequest}),
    );
  }

  getEnterpriseEditionConnectorLimits(): Observable<ConnectorLimits> {
    return from(this.edcClient.enterpriseEditionApi.connectorLimits());
  }
}
