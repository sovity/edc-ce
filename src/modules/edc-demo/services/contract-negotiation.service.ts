import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {environment} from '../../../environments/environment';
import {
  ContractNegotiationDto,
  NegotiationInitiateRequestDto,
} from '../../edc-dmgmt-client';
import {ContractOffer} from '../models/contract-offer';
import {NegotiationResult} from '../models/negotiation-result';
import {TransferProcessStates} from '../models/transfer-process-states';
import {CatalogBrowserService} from './catalog-browser.service';
import {NotificationService} from './notification.service';

interface RunningTransferProcess {
  processId: string;
  assetId?: string;
  state: TransferProcessStates;
}

@Injectable({providedIn: 'root'})
export class ContractNegotiationService {
  runningTransferProcesses: RunningTransferProcess[] = [];
  // contractOfferId, NegotiationResult
  runningNegotiations: Map<string, NegotiationResult> = new Map<
    string,
    NegotiationResult
  >();
  // contractOfferId, contractAgreementId
  finishedNegotiations: Map<string, ContractNegotiationDto> = new Map<
    string,
    ContractNegotiationDto
  >();
  private pollingHandleNegotiation?: any;

  constructor(
    private catalogBrowserService: CatalogBrowserService,
    private router: Router,
    private notificationService: NotificationService,
  ) {
    if (!environment.production) {
      // Test data on local dev
      this.runningNegotiations.set(
        'offer:6db25605-cd8c-4528-ade2-6a90349d06ac',
        {} as any,
      );
      this.finishedNegotiations.set(
        'offer:6db25605-cd8c-4528-ade1-6a90389d06ac',
        {} as any,
      );
    }
  }

  isBusy(contractOffer: ContractOffer) {
    return (
      this.runningNegotiations.get(contractOffer.id) !== undefined ||
      !!this.runningTransferProcesses.find(
        (tp) => tp.assetId === contractOffer.asset.id,
      )
    );
  }

  isNegotiated(contractOffer: ContractOffer) {
    return this.finishedNegotiations.get(contractOffer.id) !== undefined;
  }

  getState(contractOffer: ContractOffer): string {
    const transferProcess = this.runningTransferProcesses.find(
      (tp) => tp.assetId === contractOffer.asset.id,
    );
    if (transferProcess) {
      return TransferProcessStates[transferProcess.state];
    }

    const negotiation = this.runningNegotiations.get(contractOffer.id);
    if (negotiation) {
      return 'negotiating';
    }

    return '';
  }

  negotiate(contractOffer: ContractOffer) {
    const initiateRequest: NegotiationInitiateRequestDto = {
      connectorAddress: contractOffer.asset.originator!,

      offer: {
        offerId: contractOffer.id,
        assetId: contractOffer.asset.id,
        policy: contractOffer.policy,
      },
      connectorId: 'my-connector',
      protocol: 'ids-multipart',
    };

    const finishedNegotiationStates = ['CONFIRMED', 'DECLINED', 'ERROR'];

    this.catalogBrowserService.initiateNegotiation(initiateRequest).subscribe(
      (negotiationId) => {
        this.finishedNegotiations.delete(initiateRequest.offer.offerId);
        this.runningNegotiations.set(initiateRequest.offer.offerId, {
          id: negotiationId,
          offerId: initiateRequest.offer.offerId,
        });

        if (!this.pollingHandleNegotiation) {
          // there are no active negotiations
          this.pollingHandleNegotiation = setInterval(() => {
            for (const negotiation of this.runningNegotiations.values()) {
              this.catalogBrowserService
                .getNegotiationState(negotiation.id)
                .subscribe((updatedNegotiation) => {
                  if (
                    finishedNegotiationStates.includes(updatedNegotiation.state)
                  ) {
                    let offerId = negotiation.offerId;
                    this.runningNegotiations.delete(offerId);
                    if (updatedNegotiation.state === 'CONFIRMED') {
                      this.finishedNegotiations.set(
                        offerId,
                        updatedNegotiation,
                      );
                      this.notificationService.showInfo(
                        'Contract Negotiation complete!',
                        'Show me!',
                        () => {
                          this.router.navigate(['/contracts']);
                        },
                      );
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
      },
      (error) => {
        console.error(error);
        this.notificationService.showError('Error starting negotiation');
      },
    );
  }
}
