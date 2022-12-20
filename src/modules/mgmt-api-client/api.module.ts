import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { ApplicationObservabilityService } from './api/applicationObservability.service';
import { AssetService } from './api/asset.service';
import { CatalogService } from './api/catalog.service';
import { ContractAgreementService } from './api/contractAgreement.service';
import { ContractDefinitionService } from './api/contractDefinition.service';
import { ContractNegotiationService } from './api/contractNegotiation.service';
import { DataplaneSelectorService } from './api/dataplaneSelector.service';
import { HTTPProvisionerWebhookService } from './api/hTTPProvisionerWebhook.service';
import { PolicyService } from './api/policy.service';
import { TransferProcessService } from './api/transferProcess.service';

@NgModule({
  imports:      [],
  declarations: [],
  exports:      [],
  providers: []
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders<ApiModule> {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        };
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule,
                 @Optional() http: HttpClient) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
            'See also https://github.com/angular/angular/issues/20575');
        }
    }
}
