import {Component, Inject, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {CatalogBrowserService} from "../../services/catalog-browser.service";
import {ContractNegotiationDto, NegotiationInitiateRequestDto} from "../../../mgmt-api-client";
import {NotificationService} from "../../services/notification.service";
import {Router} from "@angular/router";
import {TransferProcessStates} from "../../models/transfer-process-states";
import {ContractOffer} from "../../models/contract-offer";
import {NegotiationResult} from "../../models/negotiation-result";

interface RunningTransferProcess {
  processId: string;
  assetId?: string;
  state: TransferProcessStates;
}

@Component({
  selector: 'edc-demo-catalog-browser',
  templateUrl: './catalog-browser.component.html',
  styleUrls: ['./catalog-browser.component.scss']
})
export class CatalogBrowserComponent implements OnInit {

  filteredContractOffers$: Observable<ContractOffer[]> = of([]);
  searchText = '';
  runningTransferProcesses: RunningTransferProcess[] = [];
  runningNegotiations: Map<string, NegotiationResult> = new Map<string, NegotiationResult>(); // contractOfferId, NegotiationResult
  finishedNegotiations: Map<string, ContractNegotiationDto> = new Map<string, ContractNegotiationDto>(); // contractOfferId, contractAgreementId
  private fetch$ = new BehaviorSubject(null);
  private pollingHandleNegotiation?: any;

  constructor(private apiService: CatalogBrowserService,
              public dialog: MatDialog,
              private router: Router,
              private notificationService: NotificationService,
              @Inject('HOME_CONNECTOR_STORAGE_ACCOUNT') private homeConnectorStorageAccount: string) {
  }

  ngOnInit(): void {
    this.filteredContractOffers$ = this.fetch$
      .pipe(
        switchMap(() => {
          const contractOffers$ = this.apiService.getContractOffers();
          return !!this.searchText ?
            contractOffers$.pipe(map(contractOffers => contractOffers.filter(contractOffer => contractOffer.asset.name.toLowerCase().includes(this.searchText))))
            :
            contractOffers$;
        }));
  }

  onSearch() {
    this.fetch$.next(null);
  }

  onNegotiateClicked(contractOffer: ContractOffer) {
    const initiateRequest: NegotiationInitiateRequestDto = {
      connectorAddress: contractOffer.asset.originator,

      offer: {
        offerId: contractOffer.id,
        assetId: contractOffer.asset.id,
        policy: contractOffer.policy,
      },
      connectorId: 'yomama',
      protocol: 'ids-multipart'
    };

    const finishedNegotiationStates = [
      "CONFIRMED",
      "DECLINED",
      "ERROR"];

    this.apiService.initiateNegotiation(initiateRequest).subscribe(negotiationId => {
      this.finishedNegotiations.delete(initiateRequest.offer.offerId);
      this.runningNegotiations.set(initiateRequest.offer.offerId, {
        id: negotiationId,
        offerId: initiateRequest.offer.offerId
      });

      if (!this.pollingHandleNegotiation) {
        // there are no active negotiations
        this.pollingHandleNegotiation = setInterval(() => {
          // const finishedNegotiations: NegotiationResult[] = [];

          for (const negotiation of this.runningNegotiations.values()) {
            this.apiService.getNegotiationState(negotiation.id).subscribe(updatedNegotiation => {
              if (finishedNegotiationStates.includes(updatedNegotiation.state!)) {
                let offerId = negotiation.offerId;
                this.runningNegotiations.delete(offerId);
                if (updatedNegotiation.state === "CONFIRMED") {
                  this.finishedNegotiations.set(offerId, updatedNegotiation);
                  this.notificationService.showInfo("Contract Negotiation complete!", "Show me!", () => {
                    this.router.navigate(['/contracts'])
                  })
                }
              }

              if (this.runningNegotiations.size === 0) {
                clearInterval(this.pollingHandleNegotiation);
                this.pollingHandleNegotiation = undefined;
              }
            });
          }
        }, 1000);
      }
    }, error => {
      console.error(error);
      this.notificationService.showError("Error starting negotiation");
    });
  }

  isBusy(contractOffer: ContractOffer) {
    return this.runningNegotiations.get(contractOffer.id) !== undefined || !!this.runningTransferProcesses.find(tp => tp.assetId === contractOffer.asset.id);
  }

  getState(contractOffer: ContractOffer): string {
    const transferProcess = this.runningTransferProcesses.find(tp => tp.assetId === contractOffer.asset.id);
    if (transferProcess) {
      return TransferProcessStates[transferProcess.state];
    }

    const negotiation = this.runningNegotiations.get(contractOffer.id);
    if (negotiation) {
      return 'negotiating';
    }

    return '';
  }

  isNegotiated(contractOffer: ContractOffer) {
    return this.finishedNegotiations.get(contractOffer.id) !== undefined;
  }

}
