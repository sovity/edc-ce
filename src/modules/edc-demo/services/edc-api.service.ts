import {Injectable} from '@angular/core';
import {Observable, from} from 'rxjs';
import {
  ContractAgreementPage,
  KpiResult,
  buildEdcClient,
} from '@sovity.de/edc-client';
import {AppConfigService} from '../../app/config/app-config.service';

@Injectable({providedIn: 'root'})
export class EdcApiService {
  constructor(private appConfigService: AppConfigService) {}

  getContractAgreementPage(): Observable<ContractAgreementPage> {
    return from(this.edcClient().uiApi.contractAgreementEndpoint());
  }

  getKpis(): Observable<KpiResult> {
    return from(this.edcClient().useCaseApi.kpiEndpoint());
  }

  private edcClient() {
    return buildEdcClient({
      managementApiUrl: this.appConfigService.config.managementApiUrl,
      managementApiKey: this.appConfigService.config.managementApiKey,
    });
  }
}
